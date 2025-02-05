package com.server.domain.project.repository;

import com.server.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerId(Long ownerId);

    Project findByOwnerIdAndName(Long ownerId, String name);
}
