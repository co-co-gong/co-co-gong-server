package com.server.domain.user.service;

import com.server.global.error.code.AuthErrorCode;
import com.server.global.error.exception.AuthException;
import com.server.global.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
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

    public User updateEmail(HttpServletRequest request, String email) {
        String username = jwtService.extractUsernameFromToken(request)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));

        // username으로 찾은 user 반환
        User user = getUserWithPersonalInfo(username);

        user.updateEmail(email);
        return userRepository.save(user);

    }

    public String deleteUser(HttpServletRequest request) {
        String username = jwtService.extractUsernameFromToken(request)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));

        // username으로 찾은 user 반환
        User user = getUserWithPersonalInfo(username);

        userRepository.delete(user);

        return user.getUsername();
    }

    public GetUserOutDto getUser(HttpServletRequest request) {
        // request(token)에서 username 추출
        String username = jwtService.extractUsernameFromToken(request)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));
        log.info("Extracted username: {}", username);

        // username으로 찾은 user -> userDto 반환
        return userMapper.toGetUserOutDto(getUserWithPersonalInfo(username));

    }
}
