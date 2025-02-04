package com.server.domain.project.dto;

import java.util.UUID;

import com.server.domain.project.entity.Project;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectDto {
    private String name;
    private String url;
    private String s3Path;
    private Long ownerId;


    public static ProjectDto from(Project p) {
        return ProjectDto.builder()
                .name(p.getName())
                .url(p.getUrl())
                .s3Path(p.getS3Path())
                .ownerId(p.getOwnerId())
                .build();
    }
}
