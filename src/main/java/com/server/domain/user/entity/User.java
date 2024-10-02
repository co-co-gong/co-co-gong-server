package com.server.domain.user.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

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

    @Column(name="user_thumbnail")
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


    public User(String username, String thumbnail, String email, String oauth, String githubToken){
        this.username = username;
        this.email = email;
        this.thumbnail = thumbnail;
        this.oauth = oauth;
        this.githubToken = githubToken;
    }
    }
