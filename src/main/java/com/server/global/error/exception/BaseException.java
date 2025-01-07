package com.server.global.error.exception;

import com.server.global.error.code.ErrorCode;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final int status;
    private final String message;

    public BaseException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }
}
