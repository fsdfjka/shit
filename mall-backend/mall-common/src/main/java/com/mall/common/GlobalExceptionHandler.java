package com.mall.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/** 全局异常处理：各服务 Application 需 scanBasePackages="com.mall" 以扫描到本类 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public Result<Void> handlerBiz(BizException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    /** Bean Validation 校验失败（@NotBlank/@NotNull 等）：返回字段真实信息，避免吞成"系统繁忙" */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<Void> handlerValidation(Exception e) {
        String msg = "参数校验失败";
        try {
            FieldError[] errors;
            if (e instanceof MethodArgumentNotValidException manve) {
                errors = manve.getBindingResult().getFieldErrors().toArray(new FieldError[0]);
            } else {
                errors = ((BindException) e).getBindingResult().getFieldErrors().toArray(new FieldError[0]);
            }
            if (errors.length > 0) {
                msg = errors[0].getDefaultMessage();
            }
        } catch (Exception ignore) {
            /* 解析失败回退默认文案 */
        }
        return Result.fail(400, msg);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handlerOther(Exception e) {
        log.error("系统异常", e);
        return Result.fail(500, "系统繁忙，请稍后重试");
    }
}
