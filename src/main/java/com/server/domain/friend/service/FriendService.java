package com.server.domain.friend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.friend.dto.GetFriendOutDto;
import com.server.domain.friend.entity.Friend;
import com.server.domain.friend.enums.FriendState;
import com.server.domain.friend.mapper.FriendMapper;
import com.server.domain.friend.repository.FriendRepository;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;
import com.server.global.error.code.FriendErrorCode;
import com.server.global.error.code.UserErrorCode;
import com.server.global.error.exception.BusinessException;
import com.server.global.jwt.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendService {

    // TODO: user.getRequestUser() 또는 user.getReceiptUser()를 통해 refactoring

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final FriendMapper friendMapper;
    private final JwtService jwtService;

    public List<GetFriendOutDto> getRequestUser(HttpServletRequest request, FriendState state) {
        List<Friend> friends;
        String username = jwtService.extractUsernameFromToken(request).get();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        if (state == null) {
            friends = friendRepository.findByRequestUser(user)
                    .orElseThrow(() -> new BusinessException(FriendErrorCode.REQUEST_NOT_FOUND));
        } else {
            friends = friendRepository.findByRequestUserAndState(user, state)
                    .orElseThrow(() -> new BusinessException(FriendErrorCode.REQUEST_NOT_FOUND));
        }
        List<GetFriendOutDto> getFriendOutDtos = friends.stream()
                .map(friend -> friendMapper.toGetFriendOutDto(friend.getReceiptUser(), friend.getState()))
                .collect(Collectors.toList());
        return getFriendOutDtos;
    }

    public List<GetFriendOutDto> getReceiptUser(HttpServletRequest request, FriendState state) {
        String username = jwtService.extractUsernameFromToken(request).get();
        List<Friend> friends;
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        if (state == null) {
            friends = friendRepository.findByReceiptUser(user)
                    .orElseThrow(() -> new BusinessException(FriendErrorCode.REQUEST_NOT_FOUND));
        } else {
            friends = friendRepository.findByReceiptUserAndState(user, state)
                    .orElseThrow(() -> new BusinessException(FriendErrorCode.REQUEST_NOT_FOUND));
        }
        List<GetFriendOutDto> getFriendOutDtos = friends.stream()
                .map(friend -> friendMapper.toGetFriendOutDto(friend.getRequestUser(), friend.getState()))
                .collect(Collectors.toList());
        return getFriendOutDtos;
    }

    private void validateFriendRequest(User user1, User user2) {
        Optional<Friend> friend;
        friend = friendRepository.findByRequestUserAndReceiptUser(user1, user2);
        if (friend.isPresent()) {
            if (FriendState.SENDING == friend.get().getState()) {
                throw new BusinessException(FriendErrorCode.REQUEST_ALREADY_EXISTS);
            }
            if (FriendState.ACCEPTED == friend.get().getState()) {
                throw new BusinessException(FriendErrorCode.FRIENDS_ALREADY_EXISTS);
            }
        }
        friend = friendRepository.findByRequestUserAndReceiptUser(user2, user1);
        if (friend.isPresent()) {
            if (FriendState.SENDING == friend.get().getState()) {
                throw new BusinessException(FriendErrorCode.RECEIPT_ALREADY_EXISTS);
            }
            if (FriendState.ACCEPTED == friend.get().getState()) {
                throw new BusinessException(FriendErrorCode.FRIENDS_ALREADY_EXISTS);
            }
        }
    }

    @Transactional
    public String createFriendRequest(HttpServletRequest request, String receiptUsername) {
        String requestUsername = jwtService.extractUsernameFromToken(request).get();
        User requestUser = userRepository.findByUsername(requestUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        User receiptUser = userRepository.findByUsername(receiptUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        validateFriendRequest(requestUser, receiptUser);
        Friend friend = Friend.builder()
                .requestUser(requestUser)
                .receiptUser(receiptUser)
                .state(FriendState.SENDING)
                .build();
        friendRepository.save(friend);
        return requestUsername;
    }

    @Transactional
    public String deleteFriendRequest(HttpServletRequest request, String receiptUsername) {
        String requestUsername = jwtService.extractUsernameFromToken(request).get();
        User requestUser = userRepository.findByUsername(requestUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        User receiptUser = userRepository.findByUsername(receiptUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        Friend friend = friendRepository.findByRequestUserAndReceiptUser(requestUser, receiptUser)
                .orElseThrow(() -> new BusinessException(FriendErrorCode.REQUEST_NOT_FOUND));
        if (FriendState.ACCEPTED == friend.getState()) {
            throw new BusinessException(FriendErrorCode.FRIENDS_ALREADY_EXISTS);
        }
        if (FriendState.REMOVED == friend.getState()) {
            throw new BusinessException(FriendErrorCode.REQUEST_ALREADY_REMOVED);
        }
        friend.setState(FriendState.REMOVED);
        return requestUsername;
    }

    @Transactional
    public String acceptFriendRequest(String requestUsername, HttpServletRequest request) {
        String receiptUsername = jwtService.extractUsernameFromToken(request).get();
        User requestUser = userRepository.findByUsername(requestUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        User receiptUser = userRepository.findByUsername(receiptUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        Friend friendRequested = friendRepository.findByRequestUserAndReceiptUser(requestUser, receiptUser)
                .orElseThrow(() -> new BusinessException(FriendErrorCode.REQUEST_NOT_FOUND));
        if (FriendState.REMOVED == friendRequested.getState()) {
            throw new BusinessException(FriendErrorCode.REQUEST_NOT_FOUND);
        }
        if (FriendState.ACCEPTED == friendRequested.getState()) {
            throw new BusinessException(FriendErrorCode.FRIENDS_ALREADY_EXISTS);
        }
        friendRequested.setState(FriendState.ACCEPTED);
        Optional<Friend> friendReceipted = friendRepository.findByRequestUserAndReceiptUser(receiptUser, requestUser);
        if (friendReceipted.isPresent()) {
            friendReceipted.get().setState(FriendState.ACCEPTED);
        } else {
            Friend friendApproved = Friend.builder()
                    .requestUser(receiptUser)
                    .receiptUser(requestUser)
                    .state(FriendState.ACCEPTED)
                    .build();
            friendRepository.save(friendApproved);
        }
        return receiptUsername;
    }
}
