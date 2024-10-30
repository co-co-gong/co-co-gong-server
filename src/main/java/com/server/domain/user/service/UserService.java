package com.server.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.oauth.dto.GithubDto;
import com.server.domain.user.dto.GetUserOutDto;
import com.server.domain.user.entity.User;
import com.server.domain.user.mapper.UserMapper;
import com.server.domain.user.repository.UserRepository;
import com.server.global.error.code.UserErrorCode;
import com.server.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public User loginOrRegister(GithubDto githubDto, String githubToken) {
        return userRepository.findByUsername(githubDto.getUsername())
                .orElseGet(() -> registerNewUser(githubDto, githubToken));
    }

    private User registerNewUser(GithubDto githubDto, String githubToken) {
        User newUser = User.builder()
                .email(githubDto.getEmail())
                .username(githubDto.getUsername())
                .thumbnail(githubDto.getThumbnail())
                .oauth("github")
                .githubToken(githubToken)
                .build();

        return userRepository.save(newUser);
    }

    @Transactional
    public void saveRefreshToken(String username, String refreshToken) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);
    }

    public User getUserWithPersonalInfo(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
    }

    public GetUserOutDto getUserWithoutPersonalInfo(String username) {
        User user = getUserWithPersonalInfo(username);
        return userMapper.toGetUserOutDto(user);
    }

    public User updateEmail(User user, String email) {
        user.updateEmail(email);
        return userRepository.save(user);

    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
