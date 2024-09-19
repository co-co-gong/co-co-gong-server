package com.server.domain.participant.dto;

import java.util.UUID;

import lombok.Getter;

@Getter
public class ParticipantDto {
    private UUID id;
    private UUID userId;
    private String sessionId;
    private boolean participate;
    private boolean mic;
    private String role;
}
