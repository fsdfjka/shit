package com.mall.common;

import lombok.Getter;

/** 业务异常：code 默认 400（参数/业务错误） */
@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(String message) {
        this(Result.CODE_BAD_REQUEST, message);
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }
}
