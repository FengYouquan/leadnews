package com.youquan.common.exception;

import com.youquan.model.common.enums.AppHttpCodeEnum;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/9/29 10:52
 */
public class CustomException extends RuntimeException {

    private AppHttpCodeEnum appHttpCodeEnum;

    public CustomException(AppHttpCodeEnum appHttpCodeEnum) {
        this.appHttpCodeEnum = appHttpCodeEnum;
    }

    public AppHttpCodeEnum getAppHttpCodeEnum() {
        return appHttpCodeEnum;
    }
}
