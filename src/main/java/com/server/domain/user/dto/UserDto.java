package com.server.domain.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.server.domain.user.enums.OAuth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private OAuth oauth;
    private String githubToken;
    private LocalDateTime createdAt;
}
