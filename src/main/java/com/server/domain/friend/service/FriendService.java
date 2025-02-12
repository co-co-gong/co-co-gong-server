package com.server.domain.friend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.friend.dto.GetFriendListOutDto;
import com.server.domain.friend.dto.GetFriendRequestOutDto;
import com.server.domain.friend.entity.FriendList;
import com.server.domain.friend.entity.FriendRequest;
import com.server.domain.friend.enums.FriendListState;
import com.server.domain.friend.enums.FriendRequestState;
import com.server.domain.friend.mapper.FriendMapper;
import com.server.domain.friend.repository.FriendListRepository;
import com.server.domain.friend.repository.FriendRequestRepository;
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
    private final FriendRequestRepository friendRequestRepository;
    private final FriendListRepository friendListRepository;
    private final FriendMapper friendMapper;
    private final JwtService jwtService;

    public List<GetFriendRequestOutDto> getRequestUser(HttpServletRequest request, FriendRequestState state) {
        List<FriendRequest> friendRequests;
        String username = jwtService.extractUsernameFromToken(request).get();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        if (state == null) {
            friendRequests = friendRequestRepository.findByRequestUser(user)
                    .orElseThrow(() -> new BusinessException(FriendErrorCode.REQUEST_NOT_FOUND));
        } else {
            friendRequests = friendRequestRepository.findByRequestUserAndState(user, state)
                    .orElseThrow(() -> new BusinessException(FriendErrorCode.REQUEST_NOT_FOUND));
        }
        List<GetFriendRequestOutDto> getFriendRequestOutDtos = friendRequests.stream()
                .map(friendRequest -> friendMapper.toGetFriendOutDto(friendRequest.getReceiptUser(),
                        friendRequest.getState()))
                .collect(Collectors.toList());
        return getFriendRequestOutDtos;
    }

    public List<GetFriendRequestOutDto> getReceiptUser(HttpServletRequest request, FriendRequestState state) {
        String username = jwtService.extractUsernameFromToken(request).get();
        List<FriendRequest> friendRequests;
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        if (state == null) {
            friendRequests = friendRequestRepository.findByReceiptUser(user)
                    .orElseThrow(() -> new BusinessException(FriendErrorCode.REQUEST_NOT_FOUND));
        } else {
            friendRequests = friendRequestRepository.findByReceiptUserAndState(user, state)
                    .orElseThrow(() -> new BusinessException(FriendErrorCode.REQUEST_NOT_FOUND));
        }
        List<GetFriendRequestOutDto> getFriendRequestOutDtos = friendRequests.stream()
                .map(friendRequest -> friendMapper.toGetFriendOutDto(friendRequest.getRequestUser(),
                        friendRequest.getState()))
                .collect(Collectors.toList());
        return getFriendRequestOutDtos;
    }

    private Optional<FriendRequest> getFriendRequest(User user1, User user2) {
        Optional<FriendRequest> friendRequest;
        friendRequest = friendRequestRepository.findByRequestUserAndReceiptUser(user1, user2);
        if (friendRequest.isPresent()) {
            if (FriendRequestState.SENDING == friendRequest.get().getState()) {
                throw new BusinessException(FriendErrorCode.REQUEST_ALREADY_EXISTS);
            }
            // 신청을 보내려는 대상이 거절 했을 때는 친구 신청 불가
            if (FriendRequestState.REJECTED == friendRequest.get().getState()) {
                throw new BusinessException(FriendErrorCode.REQUEST_ALREADY_REJECTED);
            }
            return friendRequest;
        }
        friendRequest = friendRequestRepository.findByRequestUserAndReceiptUser(user2, user1);
        if (friendRequest.isPresent()) {
            if (FriendRequestState.SENDING == friendRequest.get().getState()) {
                throw new BusinessException(FriendErrorCode.RECEIPT_ALREADY_EXISTS);
            }
        }
        return Optional.empty();
    }

    @Transactional
    public String createFriendRequest(HttpServletRequest request, String receiptUsername) {
        String requestUsername = jwtService.extractUsernameFromToken(request).get();
        User requestUser = userRepository.findByUsername(requestUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        User receiptUser = userRepository.findByUsername(receiptUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        Optional<FriendRequest> friendRequestValidated = getFriendRequest(requestUser, receiptUser);
        FriendRequest friendRequest;
        if (friendRequestValidated.isPresent()) {
            friendRequest = friendRequestValidated.get();
            friendRequest.setState(FriendRequestState.SENDING);
        } else {
            friendRequest = FriendRequest.builder()
                    .requestUser(requestUser)
                    .receiptUser(receiptUser)
                    .state(FriendRequestState.SENDING)
                    .build();
            friendRequestRepository.save(friendRequest);
        }
        return requestUsername;
    }

    @Transactional
    public String deleteFriendRequest(HttpServletRequest request, String receiptUsername) {
        String requestUsername = jwtService.extractUsernameFromToken(request).get();
        User requestUser = userRepository.findByUsername(requestUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        User receiptUser = userRepository.findByUsername(receiptUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        FriendRequest friendRequest = friendRequestRepository.findByRequestUserAndReceiptUser(requestUser, receiptUser)
                .orElseThrow(() -> new BusinessException(FriendErrorCode.REQUEST_NOT_FOUND));
        if (FriendRequestState.REMOVED == friendRequest.getState()) {
            throw new BusinessException(FriendErrorCode.REQUEST_ALREADY_REMOVED);
        }
        if (FriendRequestState.REJECTED == friendRequest.getState()) {
            throw new BusinessException(FriendErrorCode.REQUEST_ALREADY_REJECTED);
        }
        friendRequest.setState(FriendRequestState.REMOVED);
        return requestUsername;
    }

    @Transactional
    public String acceptFriendRequest(String requestUsername, HttpServletRequest request) {
        String receiptUsername = jwtService.extractUsernameFromToken(request).get();
        User requestUser = userRepository.findByUsername(requestUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        User receiptUser = userRepository.findByUsername(receiptUsername)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        FriendRequest friendRequested = friendRequestRepository
                .findByRequestUserAndReceiptUser(requestUser, receiptUser)
                .orElseThrow(() -> new BusinessException(FriendErrorCode.REQUEST_NOT_FOUND));
        // 이미 삭제된 친구 신청
        if (FriendRequestState.REMOVED == friendRequested.getState()) {
            throw new BusinessException(FriendErrorCode.REQUEST_NOT_FOUND);
        }
        // 거절된 친구 신청
        if (FriendRequestState.REJECTED == friendRequested.getState()) {
            throw new BusinessException(FriendErrorCode.REQUEST_ALREADY_REJECTED);
        }
        // 친구 수락을 위한 신청 삭제
        friendRequestRepository.deleteById(friendRequested.getId());
        // 친구 수락 후 리스트에 추가
        FriendList friendList = FriendList.builder().requestUser(friendRequested.getRequestUser())
                .receiptUser(friendRequested.getReceiptUser()).state(FriendListState.NEUTRAL).build();
        friendListRepository.save(friendList);
        return receiptUsername;
    }

    public List<GetFriendListOutDto> getFriends(HttpServletRequest request) {
        List<FriendList> friendLists;
        String username = jwtService.extractUsernameFromToken(request).get();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
        friendLists = user.getFriendListRequestUser();
        friendLists.addAll(user.getFriendListReceiptUser());
        List<GetFriendListOutDto> getFriendListOutDtos = friendLists.stream()
                .map(friendList -> friendMapper.toGetFriendOutDto(friendList.getRequestUser(),
                        friendList.getState()))
                .collect(Collectors.toList());
        return getFriendListOutDtos;
    }
}
