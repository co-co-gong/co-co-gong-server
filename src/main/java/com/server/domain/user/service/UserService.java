package com.server.domain.user.service;

import java.util.Optional;

import com.server.domain.oauth.dto.GithubDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;

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
