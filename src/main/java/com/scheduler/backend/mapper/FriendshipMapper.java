package com.scheduler.backend.mapper;

import com.scheduler.backend.dto.response.FriendshipResponseDto;
import com.scheduler.backend.entity.Friendship;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface FriendshipMapper {

    FriendshipResponseDto toDto(Friendship friendship);
}