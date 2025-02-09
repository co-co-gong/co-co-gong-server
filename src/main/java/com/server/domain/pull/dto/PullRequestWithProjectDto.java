package com.server.domain.pull.dto;


import com.fasterxml.jackson.databind.JsonNode;
import com.server.domain.pull.entity.PullRequest;
import com.server.domain.pull.enums.PullRequestState;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class PullRequestWithProjectDto {
    private Integer prId;

    private String url;

    private String ownerId;

    private String title;

    private String branchName;

    private PullRequestState status;

    private LocalDateTime createdAt;

    private LocalDateTime closedAt;

    private LocalDateTime mergedAt;

    public static PullRequestWithProjectDto toDto(PullRequest pullRequest){
        return PullRequestWithProjectDto.builder()
                .prId(pullRequest.getPrNumber())
                .url(pullRequest.getUrl())
                .ownerId(pullRequest.getOwnerId())
                .title(pullRequest.getTitle())
                .branchName(pullRequest.getBranchName())
                .status(pullRequest.getStatus())
                .createdAt(pullRequest.getCreatedAt())
                .mergedAt(pullRequest.getMergedAt())
                .closedAt(pullRequest.getClosedAt())
                .build();
    }
}
