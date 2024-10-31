package com.server.domain.friend.dto;

import com.server.domain.friend.enums.FriendState;
import com.server.domain.user.dto.UserDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetFriendOutDto extends UserDto {
    private FriendState state;
}
