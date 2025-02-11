package com.server.domain.user.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.domain.friend.entity.FriendList;
import com.server.domain.friend.entity.FriendRequest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "user_thumbnail")
    private String thumbnail;

    @Column(name = "email", nullable = true)
    private String email;

    @Column(name = "oauth", nullable = false)
    private String oauth;

    @Column(name = "github_token")
    private String githubToken;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @OneToMany(mappedBy = "requestUser", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<FriendRequest> friendRequestRequestUser;

    @OneToMany(mappedBy = "receiptUser", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<FriendRequest> friendRequestReceiptUser;

    @OneToMany(mappedBy = "requestUser", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<FriendList> friendListRequestUser;

    @OneToMany(mappedBy = "receiptUser", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<FriendList> friendListReceiptUser;

    @Builder
    public User(String username, String thumbnail, String email, String oauth, String githubToken) {
        this.username = username;
        this.email = email;
        this.thumbnail = thumbnail;
        this.oauth = oauth;
        this.githubToken = githubToken;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateEmail(String email) {
        this.email = email;
    }
}
