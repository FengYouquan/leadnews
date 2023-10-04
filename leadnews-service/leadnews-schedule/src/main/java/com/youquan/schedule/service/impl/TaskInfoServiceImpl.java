package com.youquan.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youquan.common.constant.ScheduleConstants;
import com.youquan.common.redis.CacheService;
import com.youquan.model.schedule.dto.TaskDto;
import com.youquan.model.schedule.pojo.TaskInfo;
import com.youquan.model.schedule.pojo.TaskInfoLogs;
import com.youquan.schedule.mapper.TaskInfoLogsMapper;
import com.youquan.schedule.mapper.TaskInfoMapper;
import com.youquan.schedule.service.TaskInfoService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/5 0:46
 */
@RequiredArgsConstructor
@Service
public class TaskInfoServiceImpl extends ServiceImpl<TaskInfoMapper, TaskInfo> implements TaskInfoService {
    private final TaskInfoMapper taskInfoMapper;
    private final TaskInfoLogsMapper taskInfoLogsMapper;
    private final CacheService cacheService;

    /**
     * 添加任务
     *
     * @param taskDto 任务信息
     * @return 任务ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long addTask(TaskDto taskDto) {
        // 添加任务到数据库中
        addTaskToDb(taskDto);
        // 添加任务到Redis
        addTaskToCache(taskDto);
        return taskDto.getTaskId();
    }

    /**
     * 按照类型和优先级拉取任务
     *
     * @param type     任务类型
     * @param priority 优先级
     * @return TaskInfo任务信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public TaskDto poll(int type, int priority) {
        String topicKey = "leadnews:" + ScheduleConstants.TOPIC + type + "_" + priority;

        // 从Redis中取任务信息
        String taskDtoJson = cacheService.lRightPop(topicKey);
        TaskDto taskDto = null;
        if (StringUtils.isNotBlank(taskDtoJson)) {
            taskDto = JSON.parseObject(taskDtoJson, TaskDto.class);
            // 更新数据库信息
            updateDb(taskDto.getTaskId(), ScheduleConstants.EXECUTED);
        }
        return taskDto;
    }

    /**
     * 取消任务
     *
     * @param taskId 任务ID
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean cancelTask(long taskId) {
        // 删除任务，更新日志
        TaskDto taskDto = updateDb(taskId, ScheduleConstants.CANCELLED);

        // 删除Redis的数据
        removeTaskFromCache(taskDto);
        return true;
    }

    /**
     * 定时刷新任务数据，将数据从数据库转移到Redis
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void reloadData() {
        // 清空缓存
        clearCache();

        //  查看小于未来五分钟的所有任务
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        List<TaskInfo> taskinfoList = taskInfoMapper.selectList(Wrappers.<TaskInfo>lambdaQuery().le(TaskInfo::getExecuteTime, calendar.getTime()));
        if (Objects.isNull(taskinfoList) || taskinfoList.isEmpty()) {
            return;
        }

        // 将小于未来五分钟的所有任务存入缓存
        taskinfoList.forEach(taskinfo -> {
            TaskDto taskDto = new TaskDto();
            BeanUtils.copyProperties(taskinfo, taskDto);
            taskDto.setExecuteTime(taskinfo.getExecuteTime().getTime());
            addTaskToCache(taskDto);
        });

    }

    /**
     * 定时刷新任务数据，将数据从ZSet转移到List
     */
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0/1 * * * ?")
    @Override
    public void refresh() {
        String token = cacheService.tryLock("Leadnews:FUTURE_TASK_SYNC", 1000 * 30);
        if (StringUtils.isBlank(token)) {
            return;
        }
        // 获取所有未来数据集合的KEY值
        Set<String> futureKeySet = cacheService.scan("leadnews:" + ScheduleConstants.FUTURE + "*");
        futureKeySet.forEach(futureKey -> {
            String suffix = futureKey.split("leadnews:" + ScheduleConstants.FUTURE)[1];
            String topicKey = "leadnews:" + ScheduleConstants.TOPIC + suffix;

            // 获取该组key下当前需要消费的任务数据
            Set<String> taskDtoJsonSet = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());
            if (taskDtoJsonSet != null && !taskDtoJsonSet.isEmpty()) {
                // 将这些任务数据添加到消费者队列中
                cacheService.refreshWithPipeline(futureKey, topicKey, taskDtoJsonSet);
            }
        });
    }

    private void clearCache() {
        // 删除缓存中未来数据集合和当前消费队列的所有Key
        Set<String> topicKeySet = cacheService.scan("leadnews:" + ScheduleConstants.TOPIC + "*");
        Set<String> futureKeySet = cacheService.scan("leadnews:" + ScheduleConstants.FUTURE + "*");
        cacheService.delete(topicKeySet);
        cacheService.delete(futureKeySet);
    }

    /**
     * 删除任务，更新任务日志
     *
     * @param taskId 任务ID
     * @param status 任务状态
     * @return TaskDto
     */
    private TaskDto updateDb(long taskId, int status) {
        // 删除任务
        this.removeById(taskId);

        TaskInfoLogs taskinfoLogs = taskInfoLogsMapper.selectById(taskId);
        taskinfoLogs.setStatus(status);
        taskInfoLogsMapper.updateById(taskinfoLogs);

        TaskDto taskDto = new TaskDto();
        BeanUtils.copyProperties(taskinfoLogs, taskDto);
        taskDto.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        return taskDto;
    }

    /**
     * 删除Redis中的任务数据
     *
     * @param taskDto 任务信息
     */
    private void removeTaskFromCache(TaskDto taskDto) {
        String topicKey = "leadnews:" + ScheduleConstants.TOPIC + taskDto.getTaskType() + "_" + taskDto.getPriority();
        String futureKey = "leadnews:" + ScheduleConstants.FUTURE + taskDto.getTaskType() + "_" + taskDto.getPriority();

        if (taskDto.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lRemove(topicKey, 0, JSON.toJSONString(taskDto));
        } else {
            cacheService.zRemove(futureKey, JSON.toJSONString(taskDto));
        }
    }

    /**
     * 保存任务信息到Redis
     *
     * @param taskDto 任务信息
     */
    private void addTaskToCache(TaskDto taskDto) {
        String topicKey = "leadnews:" + ScheduleConstants.TOPIC + taskDto.getTaskType() + "_" + taskDto.getPriority();
        String futureKey = "leadnews:" + ScheduleConstants.FUTURE + taskDto.getTaskType() + "_" + taskDto.getPriority();

        // 获取5分钟之后的时间  毫秒值
        long futureTime = LocalDateTime.now().plusMinutes(5L).toInstant(ZoneOffset.of("+8")).toEpochMilli();

        // 如果任务的执行时间小于等于当前时间，以list形式存入Redis
        if (taskDto.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lLeftPush(topicKey, JSON.toJSONString(taskDto));
        } else if (taskDto.getExecuteTime() <= futureTime) {
            // 如果任务的执行时间大于当前时间 && 小于等于预设时间（未来5分钟），以zset形式存入Redis
            cacheService.zAdd(futureKey, JSON.toJSONString(taskDto), taskDto.getExecuteTime());
        }
    }

    /**
     * 保存任务信息到数据库
     *
     * @param taskDto 任务信息
     */
    private void addTaskToDb(TaskDto taskDto) {
        // 保存任务信息到数据库
        TaskInfo taskinfo = new TaskInfo();
        BeanUtils.copyProperties(taskDto, taskinfo);
        taskinfo.setExecuteTime(new Date(taskDto.getExecuteTime()));
        this.save(taskinfo);

        // 回填TaskDto的taskID
        taskDto.setTaskId(taskinfo.getTaskId());

        // 保存任务信息日志数据到数据库
        TaskInfoLogs taskinfoLogs = new TaskInfoLogs();
        BeanUtils.copyProperties(taskinfo, taskinfoLogs);
        taskinfoLogs.setVersion(1);
        taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
        taskInfoLogsMapper.insert(taskinfoLogs);
    }
}
