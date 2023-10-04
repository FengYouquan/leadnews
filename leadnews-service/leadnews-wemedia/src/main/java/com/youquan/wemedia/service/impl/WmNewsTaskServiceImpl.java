package com.youquan.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.youquan.api.schedule.IScheduleClient;
import com.youquan.model.common.dto.ResponseResult;
import com.youquan.model.common.enums.TaskTypeEnum;
import com.youquan.model.schedule.dto.TaskDto;
import com.youquan.wemedia.service.WmNewsAutoScanService;
import com.youquan.wemedia.service.WmNewsTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/5 1:16
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WmNewsTaskServiceImpl implements WmNewsTaskService {
    private final IScheduleClient iScheduleClient;
    private final WmNewsAutoScanService wmNewsAutoScanService;

    /**
     * 添加任务
     *
     * @param newsId 文章ID
     */
    @Async
    @Override
    public void addNewsToTask(Integer newsId, Date publishTime) {
        if (newsId == null || publishTime == null) {
            return;
        }

        TaskDto taskDto = new TaskDto();
        taskDto.setExecuteTime(publishTime.getTime());
        taskDto.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
        taskDto.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        taskDto.setParameters(String.valueOf(newsId).getBytes(StandardCharsets.UTF_8));

        iScheduleClient.addTask(taskDto);
    }

    /**
     * 定时从任务队列消费数据
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    @Override
    public void scanNewsByTask() {
        log.info("定时从任务队列消费数据");
        ResponseResult<?> responseResult = iScheduleClient.pollTask(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType(),
                TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        if (!Objects.equals(responseResult.getCode(), 200) || Objects.isNull(responseResult.getData())) {
            return;
        }
        String responseJson = JSON.toJSONString(responseResult.getData());
        TaskDto taskDto = JSON.parseObject(responseJson, TaskDto.class);
        byte[] parameters = taskDto.getParameters();
        String newsIdString = new String(parameters);
        wmNewsAutoScanService.autoScanWmNews(Integer.valueOf(newsIdString));
    }
}
