package com.server.domain.owner.dto;

import java.util.UUID;

import lombok.Getter;

@Getter
public class OwnerDto {
    private UUID id;
    private UUID userId;
    private String url;
}
