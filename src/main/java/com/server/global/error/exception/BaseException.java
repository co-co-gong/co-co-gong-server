package com.server.global.error.exception;

import com.server.global.error.code.ErrorCode;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final int status;
    private final String message;

    public BaseException(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }
}
