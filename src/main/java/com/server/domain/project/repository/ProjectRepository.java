package com.server.domain.project.repository;

import com.server.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerId(Long ownerId);

    Optional<Project> findByUrl(String url);

    Optional<Project> findByName(String name);
    Project findByOwnerIdAndName(Long ownerId, String name);
}
