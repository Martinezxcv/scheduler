package com.scheduler.backend.mapper;

import com.scheduler.backend.dto.response.MeetingRequestResponseDto;
import com.scheduler.backend.entity.MeetingRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MeetingRequestMapper {

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    MeetingRequestResponseDto toDto(MeetingRequest meetingRequest);
}