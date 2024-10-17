package com.server.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponseDto<T> {
    private boolean success;
    private T data;
    private String message;

    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>(true, data, null);
    }

    public static <T> ApiResponseDto<T> failure(String message) {
        return new ApiResponseDto<>(false, null, message);
    }
}