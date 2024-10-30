package com.server.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    // TODO:: throw INVALID_ACCESS_TOKEN (related: FriendController)
    // jwtService layer? or Controller layer?
    // NOTE: jwtService layer에서 에러 핸들링을 수행하는 것이 좋을듯

    // 내 정보
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me")
    public ApiResponseDto<User> getUser(HttpServletRequest request) {
        // 디버깅을 위한 로그 추가
        log.info("Received request to /me endpoint");

        // request(token)에서 username 추출
        String username = jwtService.extractUsernameFromToken(request)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));
        log.info("Extracted username: {}", username);

        // username으로 찾은 user 반환
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));

        return ApiResponseDto.success(HttpStatus.OK.value(), user);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{username}")
    public ApiResponseDto<User> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        return ApiResponseDto.success(HttpStatus.OK.value(), user);
    }

    // 정보 수정
    // 현재 바꿀 수 있는 거 email. 추후 닉네임 추가..?
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public ApiResponseDto<String> updateUser(HttpServletRequest request, String email) {
        String username = jwtService.extractUsernameFromToken(request)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));

        // username으로 찾은 user 반환
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        User changedUser = userService.updateEmail(user, email);

        // TODO: HTTP Status Code?
        // PUT -> 201?
        return ApiResponseDto.success(HttpStatus.OK.value(), changedUser.getEmail());
    }

    // 사용자 탈퇴
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/me")
    public ApiResponseDto<String> deleteUser(HttpServletRequest request) {
        String username = jwtService.extractUsernameFromToken(request)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));

        // username으로 찾은 user 반환
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));

        userService.deleteUser(user);
        return ApiResponseDto.success(HttpStatus.OK.value(),
                String.format("Success delete user: %s", user.getUsername()));
    }
}
