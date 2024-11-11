package org.example.backend.exception;

import org.example.backend.common.BaseResponse;
import org.example.backend.common.ErrorCode;
import org.example.backend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public BaseResponse<?> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("DuplicateKeyException", e);
        // 返回一个友好的提示信息或错误代码
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "重复的键值对");
    }
}
