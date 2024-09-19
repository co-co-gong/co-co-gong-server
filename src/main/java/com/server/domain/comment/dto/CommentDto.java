package com.server.domain.comment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;

@Getter
public class CommentDto {
    private UUID id;
    private String body;
    private UUID userId;
    private UUID sessionId;
    private LocalDateTime createdAt;
}
