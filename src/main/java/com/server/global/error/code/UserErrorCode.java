package com.server.global.error.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "사용자가 존재하지 않습니다.");

    private final int status;
    private final String message;
}
