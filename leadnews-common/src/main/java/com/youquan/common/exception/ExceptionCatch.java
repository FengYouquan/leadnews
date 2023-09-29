package com.youquan.common.exception;

import com.youquan.model.common.dto.ResponseResult;
import com.youquan.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/9/29 10:53
 */
@ControllerAdvice  // 控制器增强类
@Slf4j
public class ExceptionCatch {

    /**
     * 处理不可控异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception e) {
        e.printStackTrace();
        log.error("catch exception:{}", e.getMessage());

        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    /**
     * 处理可控异常  自定义异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult exception(CustomException e) {
        log.error("catch exception:{}", e);
        return ResponseResult.errorResult(e.getAppHttpCodeEnum());
    }
}
