package com.server.domain.friend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import com.server.domain.friend.entity.Friend;
import com.server.domain.friend.enums.FriendState;
import com.server.domain.friend.mapper.FriendMapper;
import com.server.domain.friend.repository.FriendRepository;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;
import com.server.domain.user.service.UserService;
import com.server.global.jwt.JwtService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class FriendServiceTest {

    @InjectMocks
    private FriendService friendService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService UserService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private FriendMapper friendMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("본인이 다른 사용자에게 친구 신청")
    void createFriendRequest_existingUser_returnsFriendRequest() {
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
        given(userRepository.findByUsername(requestUsername)).willReturn(Optional.of(requestUser));
        given(userRepository.findByUsername(receiptUsername)).willReturn(Optional.of(receiptUser));
        given(jwtService.extractUsernameFromToken(request)).willReturn(Optional.of(requestUsername));

        /* when */
        ArgumentCaptor<Friend> friendCaptor = ArgumentCaptor.forClass(Friend.class);
        friendService.createFriendRequest(request, receiptUsername);

        /* then */
        then(friendRepository).should().save(any(Friend.class));
        then(friendRepository).should(times(1)).save(friendCaptor.capture());
        Friend savedFriend = friendCaptor.getValue();
        assertThat(savedFriend.getRequestUser()).isEqualTo(requestUser);
        assertThat(savedFriend.getReceiptUser()).isEqualTo(receiptUser);
        assertThat(savedFriend.getState()).isEqualTo(FriendState.SENDING);
    }
}
