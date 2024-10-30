package com.server.global.error.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    URL_NOT_PERMITTED(HttpStatus.FORBIDDEN.value(), "허용되지 않은 URL입니다."),
    OAUTH_PROCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "OAuth 과정 중 오류가 발생했습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 access token입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 refresh token입니다.");

    private final int status;
    private final String message;
}
