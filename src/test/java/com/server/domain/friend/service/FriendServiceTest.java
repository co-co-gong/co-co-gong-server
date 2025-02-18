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
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private FriendListRepository friendListRepository;

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
        given(friendRequestRepository.findByRequestUserAndReceiptUser(requestUser, receiptUser))
                .willReturn(Optional.empty());

        /* when */
        ArgumentCaptor<FriendRequest> friendRequestCaptor = ArgumentCaptor.forClass(FriendRequest.class);
        friendService.createFriendRequest(request, receiptUsername);

        /* then */
        then(friendRequestRepository).should(times(1)).save(friendRequestCaptor.capture());
        FriendRequest friendRequest = friendRequestCaptor.getValue();
        assertThat(friendRequest.getRequestUser()).isEqualTo(requestUser);
        assertThat(friendRequest.getReceiptUser()).isEqualTo(receiptUser);
        assertThat(friendRequest.getState()).isEqualTo(FriendRequestState.SENDING);
    }

    @Test
    @DisplayName("다른 사용자가 보낸 친구 신청 수락")
    void acceptFriendRequest_receiptUser_returnsFriendRequest() {
        /* given */
        String requestUsername = "request user";
        String receiptUsername = "receipt user";
        Long friendRequestId = 981023L;
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
        FriendRequest friendRequested = FriendRequest.builder().id(friendRequestId)
                .requestUser(requestUser).receiptUser(receiptUser)
                .state(FriendRequestState.SENDING).build();
        MockHttpServletRequest request = new MockHttpServletRequest();
        given(jwtService.extractUsernameFromToken(request)).willReturn(Optional.of(receiptUsername));
        given(userRepository.findByUsername(requestUsername)).willReturn(Optional.of(requestUser));
        given(userRepository.findByUsername(receiptUsername)).willReturn(Optional.of(receiptUser));
        given(friendRequestRepository.findByRequestUserAndReceiptUser(
                requestUser, receiptUser))
                .willReturn(Optional.of(friendRequested));

        /* when */
        ArgumentCaptor<FriendList> friendListCaptor = ArgumentCaptor.forClass(FriendList.class);
        friendService.acceptFriendRequest(requestUsername, request);

        /* then */
        then(friendRequestRepository).should(times(1)).deleteById(friendRequestId);
        then(friendListRepository).should(times(1)).save(friendListCaptor.capture());
        List<FriendList> capturedFriendList = friendListCaptor.getAllValues();
        assertThat(capturedFriendList.get(0).getState()).isEqualTo(FriendListState.NEUTRAL);
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
        FriendRequest friendRequested = FriendRequest.builder().requestUser(requestUser).receiptUser(receiptUser)
                .state(FriendRequestState.SENDING).build();
        MockHttpServletRequest request = new MockHttpServletRequest();
        given(jwtService.extractUsernameFromToken(request)).willReturn(Optional.of(requestUsername));
        given(userRepository.findByUsername(requestUsername)).willReturn(Optional.of(requestUser));
        given(userRepository.findByUsername(receiptUsername)).willReturn(Optional.of(receiptUser));
        given(friendRequestRepository.findByRequestUserAndReceiptUser(
                requestUser, receiptUser))
                .willReturn(Optional.of(friendRequested));

        /* when */
        friendService.deleteFriendRequest(request, receiptUsername);

        /* then */
        assertThat(friendRequested.getState()).isEqualTo(FriendRequestState.REMOVED);
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
        FriendRequest friendRequested = FriendRequest.builder().requestUser(requestUser).receiptUser(receiptUser)
                .state(FriendRequestState.SENDING).build();
        List<FriendRequest> friends = List.of(friendRequested);
        MockHttpServletRequest request = new MockHttpServletRequest();
        GetFriendRequestOutDto dto = new GetFriendRequestOutDto();
        dto.setUsername(requestUser.getUsername());
        dto.setEmail(requestUser.getEmail());
        dto.setState(FriendRequestState.SENDING);
        given(jwtService.extractUsernameFromToken(request)).willReturn(Optional.of(receiptUsername));
        given(userRepository.findByUsername(receiptUsername)).willReturn(Optional.of(receiptUser));
        given(friendRequestRepository.findByReceiptUser(receiptUser)).willReturn(Optional.of(friends));
        given(friendMapper.toGetFriendOutDto(requestUser,
                FriendRequestState.SENDING)).willReturn(dto);

        /* when */
        List<GetFriendRequestOutDto> dtos = friendService.getReceiptUser(request, null);

        /* then */
        assertThat(dtos).hasSize(1);
        GetFriendRequestOutDto dto0 = dtos.get(0);
        assertThat(dto0.getUsername()).isEqualTo(requestUser.getUsername());
        assertThat(dto0.getState()).isEqualTo(FriendRequestState.SENDING);
    }
}
