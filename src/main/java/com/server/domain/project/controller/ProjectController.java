package com.server.domain.project.controller;

import com.server.domain.project.dto.ProjectDto;
import com.server.domain.project.service.ProjectService;
import com.server.global.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {


    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "프로젝트 생성", description = "레포지토리의 주소와 프로젝트 이름을 입력받아 프로젝트 생성")
    public ApiResponseDto<ProjectDto> createProjects(HttpServletRequest request,
                                                     @RequestParam String repoUrl,
                                                     @RequestParam String projectName){
        return ApiResponseDto.success(HttpStatus.OK.value(),
                projectService.createProject(request, repoUrl, projectName));

    }

    @GetMapping
    @Operation(summary = "프로젝트 조회", description = "생성되어 있는 프로젝트를 조회합니다.")
    public ApiResponseDto<List<ProjectDto>> getProjects(HttpServletRequest request){
        return ApiResponseDto.success(HttpStatus.OK.value(),
                projectService.getProjects(request));
    }

    @DeleteMapping("/{projectName}")
    @Operation(summary = "프로젝트 삭제", description = "프로젝트 이름을 받아 프로젝트를 삭제합니다.")
    public ApiResponseDto<ProjectDto> deleteProject(HttpServletRequest request,
                                                          @PathVariable String projectName){
        return ApiResponseDto.success(HttpStatus.OK.value(),
                projectService.deleteProject(request, projectName));
    }

}
