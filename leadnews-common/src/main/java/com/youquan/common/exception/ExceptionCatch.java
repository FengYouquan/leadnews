package com.youquan.common.exception;

import com.youquan.model.common.dto.ResponseResult;
import com.youquan.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 控制器增强类
 *
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/9/29 10:53
 */
@ControllerAdvice
@Slf4j
public class ExceptionCatch {

    /**
     * 处理不可控异常
     *
     * @param e 异常
     * @return ResponseResult
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult<?> exception(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    /**
     * 处理可控异常  自定义异常
     *
     * @param e 异常
     * @return ResponseResult
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult<?> exception(CustomException e) {
        log.error(e.getMessage(), e);
        return ResponseResult.errorResult(e.getAppHttpCodeEnum());
    }


    /**
     * 不加 @RequestBody注解，校验失败抛出的则是 BindException
     *
     * @param bindException BindException
     * @return ResponseResult<?>
     */
    @ExceptionHandler(value = {BindException.class})
    public ResponseResult<?> exceptionHandler(BindException bindException) {
        String message = bindException.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(";"));
        log.error(message);
        return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
    }

    /**
     * 加 @RequestParam 校验失败后抛出的异常是 ConstraintViolationException
     *
     * @param constraintViolationException 异常
     * @return ResponseResult<?>
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseResult<?> exceptionHandler(ConstraintViolationException constraintViolationException) {
        log.error(constraintViolationException.getMessage(), constraintViolationException);
        String message = constraintViolationException
                .getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(";"));
        log.error(message);
        return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
    }

    /**
     * 加@RequestBody 上校验失败后抛出的异常是 MethodArgumentNotValidException 异常
     *
     * @param methodArgumentNotValidException 异常
     * @return ResponseResult<?>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult<?> exceptionHandler(MethodArgumentNotValidException methodArgumentNotValidException) {
        String message = methodArgumentNotValidException.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(";"));
        log.error(message);
        return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
    }
}
