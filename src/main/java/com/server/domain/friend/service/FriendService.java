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

import jakarta.persistence.EntityNotFoundException;
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
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        if (state == null) {
            friends = friendRepository.findByRequestUser(user)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        } else {
            friends = friendRepository.findByRequestUserAndState(user, state)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        }
        List<GetFriendOutDto> getFriendOutDtos = friends.stream()
                .map(friend -> friendMapper.toGetFriendOutDto(friend.getReceiptUser(), friend.getState()))
                .collect(Collectors.toList());
        return getFriendOutDtos;
    }

    public List<GetFriendOutDto> getReceiptUser(String username, FriendState state) {
        List<Friend> friends;
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        if (state == null) {
            friends = friendRepository.findByReceiptUser(user)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        } else {
            friends = friendRepository.findByReceiptUserAndState(user, state)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        }
        List<GetFriendOutDto> getFriendOutDtos = friends.stream()
                .map(friend -> friendMapper.toGetFriendOutDto(friend.getRequestUser(), friend.getState()))
                .collect(Collectors.toList());
        return getFriendOutDtos;
    }

    @Transactional
    public Friend createFriendRequest(String requestUsername, String receiptUsername) {
        User requestUser = userRepository.findByUsername(requestUsername)
                .orElseThrow(
                        () -> new EntityNotFoundException("Request user not found with username: " + requestUsername));
        User receiptUser = userRepository.findByUsername(receiptUsername)
                .orElseThrow(
                        () -> new EntityNotFoundException("Receipt user not found with username: " + receiptUsername));
        Friend friend = Friend.builder()
                .requestUser(requestUser)
                .receiptUser(receiptUser)
                .state(FriendState.SENDING)
                .build();
        return friendRepository.save(friend);
    }

    @Transactional
    public void deleteFriendRequest(String requestUsername, String receiptUsername) {
        // requestUser와 receiptUser를 UserRepository에서 조회
        User requestUser = userRepository.findByUsername(requestUsername)
                .orElseThrow(
                        () -> new EntityNotFoundException("Request user not found with username: " +
                                requestUsername));
        User receiptUser = userRepository.findByUsername(receiptUsername)
                .orElseThrow(
                        () -> new EntityNotFoundException("Receipt user not found with username: " +
                                receiptUsername));
        Friend friend = friendRepository.findByRequestUserAndReceiptUser(requestUser,
                receiptUser)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Friend list not found with username: " + requestUsername + ", " +
                                        receiptUsername));
        friend.setState(FriendState.REMOVED);
    }

    @Transactional
    public void approveFriendRequest(String requestUsername, String receiptUsername) {
        User requestUser = userRepository.findByUsername(requestUsername)
                .orElseThrow(
                        () -> new EntityNotFoundException("Request user not found with username: " +
                                requestUsername));
        User receiptUser = userRepository.findByUsername(receiptUsername)
                .orElseThrow(
                        () -> new EntityNotFoundException("Receipt user not found with username: " +
                                receiptUsername));
        Friend friendRequested = friendRepository.findByRequestUserAndReceiptUser(requestUser,
                receiptUser)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Friend list not found with username: " + requestUsername + ", " +
                                        receiptUsername));
        friendRequested.setState(FriendState.APPROVED);
        Friend friendApproved = Friend.builder()
                .requestUser(receiptUser)
                .receiptUser(requestUser)
                .state(FriendState.APPROVED)
                .build();
        friendRepository.save(friendApproved);
    }
}
