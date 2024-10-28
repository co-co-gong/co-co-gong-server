package com.server.global.error.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendErrorCode implements ErrorCode {
    ALREADY_FRIENDS(HttpStatus.CONFLICT.value(), "이미 친구 상태입니다."),
    ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "친구 신청을 이미 완료했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "친구 신청이 존재하지 않습니다.");

    private final int status;
    private final String message;
}
