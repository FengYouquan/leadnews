package com.youquan.common.exception;

import com.youquan.model.common.enums.AppHttpCodeEnum;
import lombok.Getter;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/9/29 10:52
 */
@Getter
public class CustomException extends RuntimeException {

    private final AppHttpCodeEnum appHttpCodeEnum;

    public CustomException(AppHttpCodeEnum appHttpCodeEnum) {
        this.appHttpCodeEnum = appHttpCodeEnum;
    }
}
