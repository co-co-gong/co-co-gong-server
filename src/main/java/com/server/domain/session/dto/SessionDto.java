package com.server.domain.session.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.server.domain.session.enums.SessionType;

import lombok.Getter;

@Getter
public class SessionDto {
    private UUID id;
    private SessionType type;
    private String title;
    private UUID projectId;
    private UUID prId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private UUID recordId;
}
