package com.server.domain.user.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.domain.user.entity.User;
import com.server.domain.user.service.UserService;
import com.server.global.dto.ApiResponseDto;
import com.server.global.error.code.AuthErrorCode;
import com.server.global.error.code.UserErrorCode;
import com.server.global.error.exception.AuthException;
import com.server.global.error.exception.BusinessException;
import com.server.global.jwt.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    private final JwtService jwtService;
    private final UserService userService;

    // 내 정보
    @GetMapping("/me")
    public ResponseEntity<ApiResponseDto<User>> getUser(HttpServletRequest request) {
        // 디버깅을 위한 로그 추가
        log.info("Received request to /me endpoint");

        // request(token)에서 username 추출
        String username = jwtService.extractUsernameFromToken(request)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TOKEN));
        log.info("Extracted username: {}", username);

        // username으로 찾은 user 반환
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));

        return ResponseEntity.ok().body(ApiResponseDto.success(HttpStatus.OK.value(), user));
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 정보 수정
    // 현재 바꿀 수 있는 거 email. 추후 닉네임 추가..?
    @PutMapping
    public ResponseEntity<?> updateUser(HttpServletRequest request, String email) {
        Optional<String> username = jwtService.extractUsernameFromToken(request);
        if (username.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        // username으로 찾은 user 반환
        Optional<User> user = userService.findByUsername(username.get());

        return user.map(value -> {
            User changedUser = userService.updateEmail(value, email);
            return ResponseEntity.ok(changedUser.getEmail());
        }).orElseGet(() -> ResponseEntity.notFound().build());

    }

    // 사용자 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteUser(HttpServletRequest request) {
        Optional<String> username = jwtService.extractUsernameFromToken(request);
        if (username.isEmpty()) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }

        // username으로 찾은 user 반환
        Optional<User> user = userService.findByUsername(username.get());
        return user.map(value -> {
            userService.deleteUser(value);
            return ResponseEntity.ok("Success delete user " + value.getUsername());
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
