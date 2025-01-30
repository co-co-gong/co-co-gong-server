package com.server.domain.pull.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.server.domain.pull.dto.GetPullRequestInfoOutDto;
import com.server.domain.pull.dto.GetPullRequestOutDto;
import com.server.domain.pull.enums.PullRequestState;
import com.server.domain.pull.service.PullRequestService;
import com.server.global.dto.ApiResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pulls")
public class PullRequestController {

    private final PullRequestService pullRequestService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userOrOrgName}/{repositoryName}")
    @Operation(summary = "레포지토리에 따른 pull requests 조회", description = "- all, open, closed의 상태로 filtering 가능</br>- Merge 완료된 pull request는 merged_at 시간 존재")
    public ApiResponseDto<List<GetPullRequestOutDto>> getPullRequests(HttpServletRequest request,
            @PathVariable String userOrOrgName, @PathVariable String repositoryName,
            @RequestParam(required = false) PullRequestState state) {
        return ApiResponseDto.success(HttpStatus.OK.value(),
                pullRequestService.getPullRequests(request, userOrOrgName, repositoryName, state));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userOrOrgName}/{repositoryName}/{pullNo}")
    @Operation(summary = "", description = "")
    public ApiResponseDto<GetPullRequestInfoOutDto> getPullRequests(HttpServletRequest request,
            @PathVariable String userOrOrgName, @PathVariable String repositoryName, @PathVariable int pullNo) {
        return ApiResponseDto.success(HttpStatus.OK.value(),
                pullRequestService.getPullRequest(request, userOrOrgName, repositoryName, pullNo));
    }
}
