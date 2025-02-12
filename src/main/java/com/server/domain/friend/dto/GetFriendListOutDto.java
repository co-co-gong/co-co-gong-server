package com.server.domain.friend.dto;

import com.server.domain.friend.enums.FriendListState;
import com.server.domain.user.dto.UserDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetFriendListOutDto extends UserDto {
    private FriendListState state;
}
