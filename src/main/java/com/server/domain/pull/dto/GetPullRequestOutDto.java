package com.server.domain.pull.dto;

import com.server.domain.pull.enums.PullRequestState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class GetPullRequestOutDto extends PullRequestDto {

}
