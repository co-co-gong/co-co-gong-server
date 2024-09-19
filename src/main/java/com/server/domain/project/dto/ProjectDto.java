package com.server.domain.project.dto;

import java.util.UUID;

import lombok.Getter;

@Getter
public class ProjectDto {
    private UUID id;
    private String url;
    private String s3Path;
}
