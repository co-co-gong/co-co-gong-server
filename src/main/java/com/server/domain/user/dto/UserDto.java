package com.server.domain.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.server.domain.user.enums.OAuth;

import lombok.Getter;

@Getter
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private OAuth oauth;
    private String githubToken;
    private LocalDateTime createdAt;
}
