package com.server.domain.friend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.domain.friend.dto.GetFriendOutDto;
import com.server.domain.friend.enums.FriendState;
import com.server.domain.friend.service.FriendService;
import com.server.domain.user.service.UserService;
import com.server.global.dto.ApiResponseDto;
import com.server.global.jwt.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/friends")
public class FriendController {

    private final JwtService jwtService;
    private final UserService userService;
    private final FriendService friendService;

    @PostMapping("/request")
    @Operation(summary = "친구 신청 생성", description = "친구 신청할 사용자의 이름을 입력하여 친구 신청 생성")
    public ResponseEntity<ApiResponseDto<String>> createFriendRequest(@RequestParam String receiptUsername,
            HttpServletRequest request) {
        String requestUsername = jwtService.extractUsernameFromToken(request).get();
        friendService.createFriendRequest(requestUsername, receiptUsername);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success("Created"));
    }

    @GetMapping("/request")
    @Operation(summary = "친구 내역 조회", description = "사용자 기준 신청 보낸 내역")
    public ResponseEntity<ApiResponseDto<List<GetFriendOutDto>>> getFriendRequest(
            @RequestParam(required = false) FriendState state,
            HttpServletRequest request) {
        String username = jwtService.extractUsernameFromToken(request).get();
        List<GetFriendOutDto> getUserOutDtos = friendService.getRequestUser(username, state);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDto.success(getUserOutDtos));
    }

    @GetMapping("/receipt")
    @Operation(summary = "친구 내역 조회", description = "사용자 기준 신청 받은 내역")
    public ResponseEntity<ApiResponseDto<List<GetFriendOutDto>>> getFriendReceipt(
            @RequestParam(required = false) FriendState state,
            HttpServletRequest request) {
        String username = jwtService.extractUsernameFromToken(request).get();
        List<GetFriendOutDto> getUserOutDtos = friendService.getReceiptUser(username, state);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDto.success(getUserOutDtos));
    }

    @DeleteMapping("/request")
    @Operation(summary = "친구 신청 취소", description = "친구 신청할 사용자의 이름을 입력하여 친구 신청 삭제")
    public ResponseEntity<ApiResponseDto<String>> deleteFriendRequest(@RequestParam String receiptUsername,
            HttpServletRequest request) {
        String requestUsername = jwtService.extractUsernameFromToken(request).get();
        friendService.deleteFriendRequest(requestUsername, receiptUsername);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDto.success("removed"));
    }

    @PutMapping("/approve")
    @Operation(summary = "친구 신청 승인", description = "친구 신청을 승인할 사용자의 이름을 입력하여 친구 신청 승인")
    public ResponseEntity<ApiResponseDto<String>> approveFriendRequest(@RequestParam String requestUsername,
            HttpServletRequest request) {
        String receiptUsername = jwtService.extractUsernameFromToken(request).get();
        friendService.approveFriendRequest(requestUsername, receiptUsername);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDto.success("approved"));
    }
}
