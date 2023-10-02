package com.youquan.wemedia.controller.v1;

import com.youquan.model.common.dto.ResponseResult;
import com.youquan.model.wemedia.dto.WmLoginDto;
import com.youquan.wemedia.service.WmUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/1 23:28
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/login")
public class LoginController {
    private final WmUserService wmUserService;

    /**
     * 自媒体端用户登录
     *
     * @param wmLoginDto 登录参数
     * @return ResponseResult
     */
    @PostMapping("/in")
    public ResponseResult<?> login(@RequestBody WmLoginDto wmLoginDto) {
        log.info("自媒体端用户登录，{}", wmLoginDto);
        return ResponseResult.okResult(wmUserService.login(wmLoginDto));
    }
}
