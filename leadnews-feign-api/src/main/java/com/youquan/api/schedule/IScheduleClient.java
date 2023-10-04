package com.youquan.api.schedule;

import com.youquan.model.common.dto.ResponseResult;
import com.youquan.model.schedule.dto.TaskDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/5 1:10
 */
@FeignClient("leadnews-schedule")
public interface IScheduleClient {
    /**
     * 添加任务
     *
     * @param taskDto 任务信息
     * @return ResponseResult<?>
     */
    @PostMapping("/api/v1/task/add")
    ResponseResult<?> addTask(@RequestBody TaskDto taskDto);

    /**
     * 按照类型和优先级来拉取任务
     *
     * @param type     任务类型
     * @param priority 任务优先级
     * @return ResponseResult
     */
    @GetMapping("/api/v1/task/poll")
    ResponseResult<?> pollTask(@RequestParam int type, @RequestParam int priority);

    /**
     * 根据任务ID取消任务
     *
     * @param taskId 任务ID
     * @return ResponseResult
     */
    @GetMapping("/api/v1/task/cancel")
    ResponseResult<?> cancelTask(@RequestParam long taskId);
}
