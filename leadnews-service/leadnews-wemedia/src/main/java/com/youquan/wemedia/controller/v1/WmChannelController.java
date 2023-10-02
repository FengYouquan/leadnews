package com.youquan.wemedia.controller.v1;

import com.youquan.model.common.dto.ResponseResult;
import com.youquan.wemedia.service.WmChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/2 12:16
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/channel")
public class WmChannelController {
    private final WmChannelService wmChannelService;

    /**
     * 查询频道列表
     *
     * @return ResponseResult
     */
    @GetMapping("/channels")
    public ResponseResult<?> list() {
        log.info("查询频道列表");
        return ResponseResult.okResult(wmChannelService.list());
    }
}
