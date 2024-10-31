package com.server.domain.friend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.server.domain.friend.dto.GetFriendOutDto;
import com.server.domain.friend.enums.FriendState;
import com.server.domain.user.entity.User;

@Mapper(componentModel = "spring")
public interface FriendMapper {

    FriendMapper INSTANCE = Mappers.getMapper(FriendMapper.class);

    GetFriendOutDto toGetFriendOutDto(User user, FriendState state);
}
