package com.server.domain.friend.dto;

import com.server.domain.friend.enums.FriendRequestState;
import com.server.domain.user.dto.UserDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetFriendRequestOutDto extends UserDto {
    private FriendRequestState state;
}
