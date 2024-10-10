package com.server.domain.user.service;

import java.util.Optional;

import com.server.domain.oauth.dto.GithubDto;
import com.server.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {


    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;


    public String loginOrRegister(GithubDto githubDto, String accessToken) {
        User user = userRepository.findByUsername(githubDto.getUsername())
                .orElseGet(() -> registerNewUser(githubDto, accessToken));

        // JWT 토큰 생성
        return jwtUtil.createToken(user.getUsername(), user.getId());
    }

    private User registerNewUser(GithubDto githubDto, String accessToken) {
        log.info("name: "+githubDto.getUsername());
        User newUser = User.builder()
                .username(githubDto.getUsername())
                .email(githubDto.getEmail())
                .githubToken(accessToken)
                .oauth("github")
                .thumbnail(githubDto.getThumbnail())
                .build();

        return userRepository.save(newUser);
    }


    public Optional<User> getUserByUsername(String username) {
        return userRepository.findById(username);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(String username) {
        userRepository.deleteById(username);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public ResponseEntity<User> login(GithubDto userInfo, String access_token) {
        User user = new User(userInfo.getUsername(), userInfo.getThumbnail(), userInfo.getEmail(), "github", access_token);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}
