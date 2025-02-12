package com.server.global.error.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendErrorCode implements ErrorCode {
    FRIENDS_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "이미 친구 상태입니다."),
    REQUEST_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "친구 신청을 이미 완료했습니다."),
    REQUEST_ALREADY_REMOVED(HttpStatus.GONE.value(), "이미 삭제된 친구 신청입니다."),
    REQUEST_ALREADY_REJECTED(HttpStatus.GONE.value(), "거절당한 친구 신청입니다."),
    RECEIPT_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "해당 사용자에게서 이미 친구 신청을 받은 상태입니다."),
    SELF_FRIEND_REQUEST_NOT_ALLOWED(HttpStatus.BAD_REQUEST.value(), "자기 자신을 친구로 추가할 수 없습니다."),
    REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "친구 신청이 존재하지 않습니다.");

    private final int status;
    private final String message;
}
