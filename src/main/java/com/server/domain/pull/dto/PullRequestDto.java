package com.server.domain.pull.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.server.domain.pull.entity.PullRequest;
import com.server.domain.pull.enums.PullRequestState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PullRequestDto {

    @JsonProperty("number")
    private Integer prId;

    @JsonProperty("html_url")
    private String url;

    @JsonProperty("user")
    private void unpackUser(JsonNode user) {
        this.ownerId = user.get("login").asText();
    }

    private String ownerId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("head")
    private void unpackHead(JsonNode head) {
        this.branchName = head.get("ref").asText();
    }

    private String branchName;

    @JsonProperty("state")
    private PullRequestState status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("closed_at")
    private LocalDateTime closedAt;

    @JsonProperty("merged_at")
    private LocalDateTime mergedAt;

    public static PullRequest toEntity(PullRequestDto pullRequestDto){

        return PullRequest.builder()
                .prNumber(pullRequestDto.getPrId())
                .url(pullRequestDto.getUrl())
                .title(pullRequestDto.getTitle())
                .ownerId(pullRequestDto.getOwnerId())
                .branchName(pullRequestDto.getBranchName())
                .status(pullRequestDto.getStatus())
                .createdAt(pullRequestDto.getCreatedAt())
                .closedAt(pullRequestDto.getClosedAt())
                .mergedAt(pullRequestDto.getMergedAt())
                .build();
    }




}

