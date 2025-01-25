package com.server.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.server.domain.user.dto.GetUserOutDto;
import com.server.domain.user.entity.User;
import com.server.domain.user.service.UserService;
import com.server.global.dto.ApiResponseDto;
import com.server.global.error.code.AuthErrorCode;
import com.server.global.error.exception.AuthException;
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
    @Operation(summary = "자기 정보 얻기", description = "로그인한 유저의 정보 반환")
    public ApiResponseDto<GetUserOutDto> getUser(HttpServletRequest request) {
        // 디버깅을 위한 로그 추가
        log.info("Received request to /me endpoint");

        GetUserOutDto user = userService.getUser(request);
        return ApiResponseDto.success(HttpStatus.OK.value(), user);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{username}")
    @Operation(summary = "특정 유저의 정보 얻기", description = "username 입력 시 해당하는 유저의 정보 반환")
    public ApiResponseDto<GetUserOutDto> getUserByUsername(@PathVariable String username) {
        GetUserOutDto user = userService.getUserWithoutPersonalInfo(username);
        return ApiResponseDto.success(HttpStatus.OK.value(), user);
    }

    // 정보 수정
    // 현재 바꿀 수 있는 거 email. 추후 닉네임 추가..?
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @Operation(summary = "유저 email 수정", description = "email 수정")
    public ApiResponseDto<String> updateUser(HttpServletRequest request, String email) {
        User changedUser = userService.updateEmail(request, email);

        // TODO: HTTP Status Code?
        // PUT -> 201?
        return ApiResponseDto.success(HttpStatus.OK.value(), changedUser.getEmail());
    }

    // 사용자 탈퇴
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/me")
    @Operation(summary = "유저 삭제(탈퇴)", description = "로그인한 유저 삭제")
    public ApiResponseDto<String> deleteUser(HttpServletRequest request) {
        String name = userService.deleteUser(request);
        return ApiResponseDto.success(HttpStatus.OK.value(),
                String.format("Success delete user: %s", name));
    }
}
