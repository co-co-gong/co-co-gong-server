package com.server.global.error.exception;

import com.server.global.error.code.ErrorCode;

import lombok.Getter;

@Getter
public class AuthException extends BaseException {
    public AuthException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
