package com.czh.usercenter.exception;

import com.czh.usercenter.common.BaseResponse;
import com.czh.usercenter.common.ErrorCode;
import com.czh.usercenter.common.ResultUtils;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e){
        return ResultUtils.error(e.getCode(),e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e){
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"");

    }
}
