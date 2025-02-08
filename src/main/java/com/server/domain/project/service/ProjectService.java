package com.server.domain.project.service;

import com.server.domain.project.dto.ProjectDto;
import com.server.domain.project.entity.Project;
import com.server.domain.project.repository.ProjectRepository;
import com.server.domain.pull.enums.PullRequestState;
import com.server.domain.pull.service.PullRequestService;
import com.server.domain.user.entity.User;
import com.server.domain.user.service.UserService;
import com.server.global.error.code.AuthErrorCode;
import com.server.global.error.exception.AuthException;
import com.server.global.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final UserService userService;
    private final JwtService jwtService;
    private final ProjectRepository projectRepository;
    private final PullRequestService pullRequestService;

    public ProjectDto createProject(HttpServletRequest request, String repoUrl, String name) {
        String username = jwtService.extractUsernameFromToken(request)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));
        User user = userService.getUserWithPersonalInfo(username);
        String afterCom = repoUrl.substring(repoUrl.indexOf(".com/") + 5);
        String[] parts = afterCom.split("/");

        Project p =  projectRepository.save(Project.builder()
                .name(name)
                .url(repoUrl)
                .s3Path("")
                .ownerId(user.getId())
                .build());
        pullRequestService.getPullRequestsWithProject(user, parts[0], parts[1], PullRequestState.ALL);

        return ProjectDto.from(p);
    }

    public List<ProjectDto> getProjects(HttpServletRequest request) {
        String username = jwtService.extractUsernameFromToken(request)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));
        User user = userService.getUserWithPersonalInfo(username);

        List<Project> projects = projectRepository.findByOwnerId(user.getId());

        return projects.stream()
                .map(ProjectDto::from)
                .collect(Collectors.toList());
    }

    public ProjectDto deleteProject(HttpServletRequest request, String projectName) {
        String username = jwtService.extractUsernameFromToken(request)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));
        User user = userService.getUserWithPersonalInfo(username);

        Project p = projectRepository.findByOwnerIdAndName(user.getId(), projectName);

        ProjectDto projectDto = ProjectDto.from(p);
        projectRepository.delete(p);

        return projectDto;
    }
}
