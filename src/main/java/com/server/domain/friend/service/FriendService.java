package com.server.domain.friend.service;

import java.util.List;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final FriendMapper friendMapper;

    public List<GetFriendOutDto> getRequestUser(String username, FriendState state) {
        List<Friend> friends;
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        if (state == null) {
            friends = friendRepository.findByRequestUser(user)
                    .orElseThrow(() -> new BusinessException(FriendErrorCode.NOT_FOUND));
        } else {
            friends = friendRepository.findByRequestUserAndState(user, state)
                    .orElseThrow(() -> new BusinessException(FriendErrorCode.NOT_FOUND));
        }
        List<GetFriendOutDto> getFriendOutDtos = friends.stream()
                .map(friend -> friendMapper.toGetFriendOutDto(friend.getReceiptUser(), friend.getState()))
                .collect(Collectors.toList());
        return getFriendOutDtos;
    }

    public List<GetFriendOutDto> getReceiptUser(String username, FriendState state) {
        List<Friend> friends;
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        if (state == null) {
            friends = friendRepository.findByReceiptUser(user)
                    .orElseThrow(() -> new BusinessException(FriendErrorCode.NOT_FOUND));
        } else {
            friends = friendRepository.findByReceiptUserAndState(user, state)
                    .orElseThrow(() -> new BusinessException(FriendErrorCode.NOT_FOUND));
        }
        List<GetFriendOutDto> getFriendOutDtos = friends.stream()
                .map(friend -> friendMapper.toGetFriendOutDto(friend.getRequestUser(), friend.getState()))
                .collect(Collectors.toList());
        return getFriendOutDtos;
    }

    @Transactional
    public Friend createFriendRequest(String requestUsername, String receiptUsername) {
        User requestUser = userRepository.findByUsername(requestUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        User receiptUser = userRepository.findByUsername(receiptUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        Friend friend = Friend.builder()
                .requestUser(requestUser)
                .receiptUser(receiptUser)
                .state(FriendState.SENDING)
                .build();
        return friendRepository.save(friend);
    }

    @Transactional
    public void deleteFriendRequest(String requestUsername, String receiptUsername) {
        User requestUser = userRepository.findByUsername(requestUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        User receiptUser = userRepository.findByUsername(receiptUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        Friend friend = friendRepository.findByRequestUserAndReceiptUser(requestUser, receiptUser)
                .orElseThrow(() -> new BusinessException(FriendErrorCode.NOT_FOUND));
        friend.setState(FriendState.REMOVED);
    }

    @Transactional
    public void approveFriendRequest(String requestUsername, String receiptUsername) {
        User requestUser = userRepository.findByUsername(requestUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        User receiptUser = userRepository.findByUsername(receiptUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        Friend friendRequested = friendRepository.findByRequestUserAndReceiptUser(requestUser, receiptUser)
                .orElseThrow(() -> new BusinessException(FriendErrorCode.NOT_FOUND));
        friendRequested.setState(FriendState.APPROVED);
        Friend friendApproved = Friend.builder()
                .requestUser(receiptUser)
                .receiptUser(requestUser)
                .state(FriendState.APPROVED)
                .build();
        friendRepository.save(friendApproved);
    }
}
