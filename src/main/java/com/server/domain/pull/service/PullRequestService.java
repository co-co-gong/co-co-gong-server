package com.server.domain.pull.service;

import java.util.List;
import java.util.stream.Collectors;

import com.server.domain.project.entity.Project;
import com.server.domain.project.repository.ProjectRepository;
import com.server.domain.pull.dto.PullRequestDto;
import com.server.domain.pull.dto.PullRequestWithProjectDto;
import com.server.domain.pull.entity.PullRequest;
import com.server.domain.pull.repository.PullRequestRepository;
import com.server.global.error.code.ProjectErrorCode;
import com.server.global.error.exception.BusinessException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.server.domain.pull.dto.GetPullRequestInfoOutDto;
import com.server.domain.pull.dto.GetPullRequestOutDto;
import com.server.domain.pull.enums.PullRequestState;
import com.server.domain.user.entity.User;
import com.server.domain.user.service.UserService;
import com.server.global.error.code.AuthErrorCode;
import com.server.global.error.exception.AuthException;
import com.server.global.jwt.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PullRequestService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PullRequestRepository pullRequestRepository;
    private final ProjectRepository projectRepository;

    public List<GetPullRequestOutDto> getPullRequestsWithProject(User user, String userOrOrgName,
            String repositoryName, PullRequestState state) {

        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("https://api.github.com/repos/%s/%s/pulls", userOrOrgName, repositoryName);
        if (state != null) {
            url = String.format("%s?state=%s", url, state.getValue());
        }
        ResponseEntity<List<GetPullRequestOutDto>> githubPullRequests = restTemplate.exchange(
                url,
                HttpMethod.GET,
                buildGithubHttpEntity(user.getGithubToken()),
                new ParameterizedTypeReference<List<GetPullRequestOutDto>>() {
                });
        if (githubPullRequests.getStatusCode().value() != 200) {
            throw new AuthException(AuthErrorCode.OAUTH_PROCESS_ERROR);
        }

        List<PullRequest> pullRequests = githubPullRequests.getBody().stream()
                        .map(PullRequestDto::toEntity)
                                .toList();

        String repoUrl = String.format("https://github.com/%s/%s", userOrOrgName, repositoryName);

        Project project = projectRepository.findByUrl(repoUrl).orElseThrow(
                ()->new BusinessException(ProjectErrorCode.NOT_FOUND)
        );

        pullRequests.forEach(pullRequest -> {
            pullRequest.setProject(project);
        });

        pullRequestRepository.saveAll(pullRequests);

        return githubPullRequests.getBody();
    }

    public List<GetPullRequestOutDto> getPullRequests(HttpServletRequest request, String userOrOrgName,
                                                      String repositoryName, PullRequestState state) {
        String username = jwtService.extractUsernameFromToken(request)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));
        User user = userService.getUserWithPersonalInfo(username);

        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("https://api.github.com/repos/%s/%s/pulls", userOrOrgName, repositoryName);
        if (state != null) {
            url = String.format("%s?state=%s", url, state.getValue());
        }
        ResponseEntity<List<GetPullRequestOutDto>> githubPullRequests = restTemplate.exchange(
                url,
                HttpMethod.GET,
                buildGithubHttpEntity(user.getGithubToken()),
                new ParameterizedTypeReference<List<GetPullRequestOutDto>>() {
                });
        if (githubPullRequests.getStatusCode().value() != 200) {
            throw new AuthException(AuthErrorCode.OAUTH_PROCESS_ERROR);
        }

        return githubPullRequests.getBody();
    }


    public GetPullRequestInfoOutDto getPullRequest(HttpServletRequest request, String userOrOrgName,
            String repositoryName, int pullNo) {
        String username = jwtService.extractUsernameFromToken(request)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));
        User user = userService.getUserWithPersonalInfo(username);

        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("https://api.github.com/repos/%s/%s/pulls/%s", userOrOrgName, repositoryName,
                pullNo);
        ResponseEntity<GetPullRequestInfoOutDto> githubPullRequest = restTemplate.exchange(
                url,
                HttpMethod.GET,
                buildGithubHttpEntity(user.getGithubToken()),
                GetPullRequestInfoOutDto.class);
        return githubPullRequest.getBody();
    }

    // TODO: 아래 endpoint에서 어떤 파일들이 어떻게 변경 되었는지 파악할 수 있음
    // String url =
    // String.format("https://api.github.com/repos/%s/%s/pulls/%s/files",
    // userOrOrgName, repositoryName,
    // pullNo);

    private HttpEntity<MultiValueMap<String, String>> buildGithubHttpEntity(String accessToken) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.github+json");
        headers.add("X-GitHub-Api-Version", "2022-11-28");
        headers.setBearerAuth(accessToken);
        return new HttpEntity<>(params, headers);
    }

    public List<PullRequestWithProjectDto> getPullRequestByProjectName(HttpServletRequest request, String projectName) {
        String username = jwtService.extractUsernameFromToken(request)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));
        User user = userService.getUserWithPersonalInfo(username);

        Project p = projectRepository.findByName(projectName).orElseThrow(()-> new BusinessException(ProjectErrorCode.NOT_FOUND));


        List<PullRequest> pullRequests =  pullRequestRepository.findByProject(p);

        return pullRequests.stream()
                .map(PullRequestWithProjectDto::toDto)
                .toList();

    }
}
