package com.server.domain.friend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import com.server.domain.friend.dto.GetFriendOutDto;
import com.server.domain.friend.entity.Friend;
import com.server.domain.friend.enums.FriendState;
import com.server.domain.friend.mapper.FriendMapper;
import com.server.domain.friend.repository.FriendRepository;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;
import com.server.global.jwt.JwtService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @InjectMocks
    private FriendService friendService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private FriendMapper friendMapper;

    @Test
    @DisplayName("본인이 다른 사용자에게 친구 신청")
    void createFriendRequest_requestUser_returnsFriendRequest() {
        /* given */
        String requestUsername = "request user";
        String receiptUsername = "receipt user";
        User requestUser = User.builder()
                .username(requestUsername)
                .email("user1@example.com")
                .thumbnail("https://avatars.githubusercontent.com/u/0?v=4")
                .oauth("github")
                .githubToken("gho1234")
                .build();
        User receiptUser = User.builder()
                .username(receiptUsername)
                .email("user2@example.com")
                .thumbnail("https://avatars.githubusercontent.com/u/0?v=4")
                .oauth("github")
                .githubToken("gho4321")
                .build();
        MockHttpServletRequest request = new MockHttpServletRequest();
        given(jwtService.extractUsernameFromToken(request)).willReturn(Optional.of(requestUsername));
        given(userRepository.findByUsername(requestUsername)).willReturn(Optional.of(requestUser));
        given(userRepository.findByUsername(receiptUsername)).willReturn(Optional.of(receiptUser));

        /* when */
        ArgumentCaptor<Friend> friendCaptor = ArgumentCaptor.forClass(Friend.class);
        friendService.createFriendRequest(request, receiptUsername);

        /* then */
        then(friendRepository).should(times(1)).save(friendCaptor.capture());
        Friend savedFriend = friendCaptor.getValue();
        assertThat(savedFriend.getRequestUser()).isEqualTo(requestUser);
        assertThat(savedFriend.getReceiptUser()).isEqualTo(receiptUser);
        assertThat(savedFriend.getState()).isEqualTo(FriendState.SENDING);
    }

    @Test
    @DisplayName("다른 사용자가 보낸 친구 신청 수락")
    void acceptFriendRequest_receiptUser_returnsFriendRequest() {
        /* given */
        String requestUsername = "request user";
        String receiptUsername = "receipt user";
        User requestUser = User.builder()
                .username(requestUsername)
                .email("user1@example.com")
                .thumbnail("https://avatars.githubusercontent.com/u/0?v=4")
                .oauth("github")
                .githubToken("gho1234")
                .build();
        User receiptUser = User.builder()
                .username(receiptUsername)
                .email("user2@example.com")
                .thumbnail("https://avatars.githubusercontent.com/u/0?v=4")
                .oauth("github")
                .githubToken("gho4321")
                .build();
        Friend friendRequested = Friend.builder().requestUser(requestUser).receiptUser(receiptUser)
                .state(FriendState.SENDING).build();
        MockHttpServletRequest request = new MockHttpServletRequest();
        given(jwtService.extractUsernameFromToken(request)).willReturn(Optional.of(receiptUsername));
        given(userRepository.findByUsername(requestUsername)).willReturn(Optional.of(requestUser));
        given(userRepository.findByUsername(receiptUsername)).willReturn(Optional.of(receiptUser));
        given(friendRepository.findByRequestUserAndReceiptUser(requestUser, receiptUser))
                .willReturn(Optional.of(friendRequested));
        given(friendRepository.findByRequestUserAndReceiptUser(receiptUser, requestUser))
                .willReturn(Optional.empty());

        /* when */
        ArgumentCaptor<Friend> friendCaptor = ArgumentCaptor.forClass(Friend.class);
        friendService.acceptFriendRequest(requestUsername, request);

        /* then */
        assertThat(friendRequested.getState()).isEqualTo(FriendState.ACCEPTED);
        then(friendRepository).should(times(1)).save(friendCaptor.capture());
        List<Friend> capturedFriends = friendCaptor.getAllValues();
        assertThat(capturedFriends.get(0).getState()).isEqualTo(FriendState.ACCEPTED);
    }

    @Test
    @DisplayName("본인이 보낸 친구 신청 삭제")
    void deleteFriendRequest_requestUser_returnsFriendRequest() {
        /* given */
        String requestUsername = "request user";
        String receiptUsername = "receipt user";
        User requestUser = User.builder()
                .username(requestUsername)
                .email("user1@example.com")
                .thumbnail("https://avatars.githubusercontent.com/u/0?v=4")
                .oauth("github")
                .githubToken("gho1234")
                .build();
        User receiptUser = User.builder()
                .username(receiptUsername)
                .email("user2@example.com")
                .thumbnail("https://avatars.githubusercontent.com/u/0?v=4")
                .oauth("github")
                .githubToken("gho4321")
                .build();
        Friend friendRequested = Friend.builder().requestUser(requestUser).receiptUser(receiptUser)
                .state(FriendState.SENDING).build();
        MockHttpServletRequest request = new MockHttpServletRequest();
        given(jwtService.extractUsernameFromToken(request)).willReturn(Optional.of(requestUsername));
        given(userRepository.findByUsername(requestUsername)).willReturn(Optional.of(requestUser));
        given(userRepository.findByUsername(receiptUsername)).willReturn(Optional.of(receiptUser));
        given(friendRepository.findByRequestUserAndReceiptUser(requestUser, receiptUser))
                .willReturn(Optional.of(friendRequested));

        /* when */
        friendService.deleteFriendRequest(request, receiptUsername);

        /* then */
        assertThat(friendRequested.getState()).isEqualTo(FriendState.REMOVED);
    }

    @Test
    @DisplayName("타인이 친구 신청했을 때 본인이 친구 신청 목록 조회")
    void getFriendRequest_receiptUser_returnsFriendRequest() {
        /* given */
        String requestUsername = "request user";
        String receiptUsername = "receipt user";
        User requestUser = User.builder()
                .username(requestUsername)
                .email("user1@example.com")
                .thumbnail("https://avatars.githubusercontent.com/u/0?v=4")
                .oauth("github")
                .githubToken("gho1234")
                .build();
        User receiptUser = User.builder()
                .username(receiptUsername)
                .email("user2@example.com")
                .thumbnail("https://avatars.githubusercontent.com/u/0?v=4")
                .oauth("github")
                .githubToken("gho4321")
                .build();
        Friend friendRequested = Friend.builder().requestUser(requestUser).receiptUser(receiptUser)
                .state(FriendState.SENDING).build();
        List<Friend> friends = List.of(friendRequested);
        MockHttpServletRequest request = new MockHttpServletRequest();
        GetFriendOutDto dto = new GetFriendOutDto();
        dto.setUsername(requestUser.getUsername());
        dto.setEmail(requestUser.getEmail());
        dto.setState(FriendState.SENDING);
        given(jwtService.extractUsernameFromToken(request)).willReturn(Optional.of(receiptUsername));
        given(userRepository.findByUsername(receiptUsername)).willReturn(Optional.of(receiptUser));
        given(friendRepository.findByReceiptUser(receiptUser)).willReturn(Optional.of(friends));
        given(friendMapper.toGetFriendOutDto(requestUser, FriendState.SENDING)).willReturn(dto);

        /* when */
        List<GetFriendOutDto> dtos = friendService.getReceiptUser(request, null);

        /* then */
        assertThat(dtos).hasSize(1);
        GetFriendOutDto dto0 = dtos.get(0);
        assertThat(dto0.getUsername()).isEqualTo(requestUser.getUsername());
        assertThat(dto0.getState()).isEqualTo(FriendState.SENDING);
    }
}
