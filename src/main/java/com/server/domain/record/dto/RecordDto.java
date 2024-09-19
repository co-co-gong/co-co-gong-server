package com.server.domain.record.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;

@Getter
public class RecordDto {
    private UUID id;
    private String s3Path;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
