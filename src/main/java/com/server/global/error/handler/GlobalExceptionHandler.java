package com.server.global.error.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.server.global.dto.ApiResponseDto;
import com.server.global.error.exception.AuthException;
import com.server.global.error.exception.BusinessException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleFriendException(AuthException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiResponseDto.error(e.getStatus(), e.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleFriendException(BusinessException e) {
        return ResponseEntity.status(e.getStatus()).body(ApiResponseDto.error(e.getStatus(), e.getMessage()));
    }

}
