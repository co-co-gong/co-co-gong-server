package com.server.domain.pull.entity;

import com.server.domain.project.entity.Project;
import com.server.domain.pull.enums.PullRequestState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pull_requests")
@Builder
public class PullRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pull_request_id")
    private Long id;

    @Column(name = "pr_number")
    private Integer prNumber;

    @Column(name = "pull_request_url")
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;

    @Column(name = "pull_request_title")
    private String title;

    @Column(name = "pull_request_user_id")
    private String ownerId;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "state")
    private PullRequestState status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "merged_at")
    private LocalDateTime mergedAt;


    public void setProject(Project project){
        this.project = project;
    }
}
