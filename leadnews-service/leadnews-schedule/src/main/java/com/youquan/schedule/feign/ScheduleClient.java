package com.youquan.schedule.feign;

import com.youquan.api.schedule.IScheduleClient;
import com.youquan.model.common.dto.ResponseResult;
import com.youquan.model.schedule.dto.TaskDto;
import com.youquan.schedule.service.TaskInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/5 1:11
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class ScheduleClient implements IScheduleClient {
    private final TaskInfoService taskInfoService;

    /**
     * 添加任务
     *
     * @param taskDto 任务信息
     * @return ResponseResult<?>
     */
    @PostMapping("/api/v1/task/add")
    @Override
    public ResponseResult<?> addTask(TaskDto taskDto) {
        log.info("添加任务，{}", taskDto);
        return ResponseResult.okResult(taskInfoService.addTask(taskDto));
    }

    /**
     * 按照类型和优先级来拉取任务
     *
     * @param type     任务类型
     * @param priority 任务优先级
     * @return ResponseResult
     */
    @GetMapping("/api/v1/task/poll")
    @Override
    public ResponseResult<?> pollTask(int type, int priority) {
        return ResponseResult.okResult(taskInfoService.poll(type, priority));
    }

    /**
     * 根据任务ID取消任务
     *
     * @param taskId 任务ID
     * @return ResponseResult
     */
    @GetMapping("/api/v1/task/cancel")
    @Override
    public ResponseResult<?> cancelTask(long taskId) {
        return ResponseResult.okResult(taskInfoService.cancelTask(taskId));
    }
}
