package com.scheduler.backend.mapper;

import com.scheduler.backend.dto.response.UserResponseDto;
import com.scheduler.backend.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toDto(User user);
}