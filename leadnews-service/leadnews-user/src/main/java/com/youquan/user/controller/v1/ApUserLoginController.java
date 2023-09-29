package com.youquan.user.controller.v1;

import com.youquan.common.config.Knife4jConfig;
import com.youquan.model.common.dto.ResponseResult;
import com.youquan.model.user.dto.ApUserLoginDto;
import com.youquan.user.service.ApUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/9/29 12:09
 */
@Api(tags = "APP端用户登录模块")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/login")
public class ApUserLoginController {
    private final ApUserService apUserService;

    /**
     * App端用户登录
     *
     * @param apUserLoginDto 登录参数
     * @return ResponseResult
     */
    @ApiOperation("用户登录功能")
    @PostMapping("/login_auth")
    public ResponseResult<?> login(@RequestBody @Validated ApUserLoginDto apUserLoginDto) {
        log.info("APP端用户登录，{}", apUserLoginDto);
        return ResponseResult.okResult(apUserService.login(apUserLoginDto));
    }
}
