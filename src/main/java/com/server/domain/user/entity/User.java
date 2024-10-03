package com.server.domain.user.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_name", nullable = false, unique = true)
    private String username;

    @Column(name = "user_thumbnail")
    private String thumbnail;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "oauth", nullable = false)
    private String oauth;

    @Column(name = "github_token")
    private String githubToken;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    public User(String username, String thumbnail, String email, String oauth, String githubToken) {
        this.username = username;
        this.email = email;
        this.thumbnail = thumbnail;
        this.oauth = oauth;
        this.githubToken = githubToken;
    }
}
