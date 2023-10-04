package com.youquan.model.schedule.dto;

import lombok.Data;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/5 0:42
 */
@Data
public class TaskDto {
    /**
     * 任务id
     */
    private Long taskId;
    /**
     * 类型
     */
    private Integer taskType;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 执行id
     */
    private long executeTime;

    /**
     * task参数
     */
    private byte[] parameters;
}
