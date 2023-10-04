package com.youquan.schedule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youquan.model.schedule.dto.TaskDto;
import com.youquan.model.schedule.pojo.TaskInfo;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/5 0:42
 */
public interface TaskInfoService extends IService<TaskInfo> {
    /**
     * 添加延迟任务
     *
     * @param taskDto 任务信息
     * @return Long 任务ID
     */
    Long addTask(TaskDto taskDto);

    /**
     * 按照类型和优先级拉取任务
     *
     * @param type     任务类型
     * @param priority 优先级
     * @return TaskInfo任务信息
     */
    TaskDto poll(int type, int priority);

    /**
     * 取消任务
     *
     * @param taskId 任务ID
     * @return boolean
     */
    boolean cancelTask(long taskId);

    /**
     * 定时刷新任务数据，将数据从ZSet转移到List
     */
    void refresh();

    /**
     * 定时刷新任务数据，将数据从数据库转移到Redis
     */
    void reloadData();
}
