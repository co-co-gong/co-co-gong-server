package com.server.global.error.exception;

import com.server.global.error.code.ErrorCode;

import lombok.Getter;

@Getter
public class BusinessException extends BaseException {
    public BusinessException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
