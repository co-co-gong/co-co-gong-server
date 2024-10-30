package com.server.domain.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.server.domain.user.dto.GetUserOutDto;
import com.server.domain.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    GetUserOutDto toGetUserOutDto(User user);
}
