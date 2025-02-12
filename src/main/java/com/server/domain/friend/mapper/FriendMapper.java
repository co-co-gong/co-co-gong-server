package com.server.domain.friend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.server.domain.friend.dto.GetFriendListOutDto;
import com.server.domain.friend.dto.GetFriendRequestOutDto;
import com.server.domain.friend.enums.FriendListState;
import com.server.domain.friend.enums.FriendRequestState;
import com.server.domain.user.entity.User;

@Mapper(componentModel = "spring")
public interface FriendMapper {

    FriendMapper INSTANCE = Mappers.getMapper(FriendMapper.class);

    GetFriendRequestOutDto toGetFriendOutDto(User user, FriendRequestState state);

    GetFriendListOutDto toGetFriendOutDto(User user, FriendListState state);
}
