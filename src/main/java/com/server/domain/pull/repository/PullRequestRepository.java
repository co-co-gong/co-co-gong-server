package com.server.domain.pull.repository;

import com.server.domain.project.entity.Project;
import com.server.domain.pull.entity.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {
    List<PullRequest> findByProject(Project project);
}
