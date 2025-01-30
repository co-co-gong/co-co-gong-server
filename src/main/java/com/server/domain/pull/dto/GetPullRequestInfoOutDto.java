package com.server.domain.pull.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetPullRequestInfoOutDto extends PullRequestDto {

    @JsonProperty("merged")
    Boolean merged;

    @JsonProperty("mergeable")
    Boolean mergeable;

    @JsonProperty("mergeable_state")
    String mergeableState;

    // NOTE: 일반 댓글 (comments)는 고려하지 않고 리뷰 댓글 (review_comments)만 고려
    @JsonProperty("review_comments")
    int comments;

    @JsonProperty("commits")
    int commits;

    @JsonProperty("additions")
    int additions;

    @JsonProperty("deletions")
    int deletions;

    @JsonProperty("changed_files")
    int changedFiles;

}
