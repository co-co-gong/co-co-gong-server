package com.server.domain.pullrequest.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.server.domain.pullrequest.enums.PullRequestStatus;

import lombok.Getter;

@Getter
public class PullRequestDto {
    private UUID id;
    private Integer prId;
    private String url;
    private String ownerId;
    private String title;
    private String description;
    private String branchName;
    private PullRequestStatus status;
    private LocalDateTime createdAt;
}
