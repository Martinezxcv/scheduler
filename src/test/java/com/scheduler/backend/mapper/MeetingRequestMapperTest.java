package com.scheduler.backend.mapper;

import com.scheduler.backend.dto.response.MeetingRequestResponseDto;
import com.scheduler.backend.entity.MeetingRequest;
import com.scheduler.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MeetingRequestMapperTest {

    @Autowired
    private MeetingRequestMapper meetingRequestMapper;

    @Test
    void shouldMapMeetingRequestToMeetingRequestResponseDto() {
        // Given
        UUID requestId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        OffsetDateTime startTime = OffsetDateTime.now().plusDays(1);
        OffsetDateTime endTime = startTime.plusHours(1);
        OffsetDateTime createdAt = OffsetDateTime.now();

        User requester = new User();
        requester.setId(requesterId);

        User receiver = new User();
        receiver.setId(receiverId);

        MeetingRequest meetingRequest = new MeetingRequest();
        meetingRequest.setId(requestId);
        meetingRequest.setRequester(requester);
        meetingRequest.setReceiver(receiver);
        meetingRequest.setStartTime(startTime);
        meetingRequest.setEndTime(endTime);
        meetingRequest.setTitle("Spotkanie projektowe");
        meetingRequest.setStatus("PENDING");
        meetingRequest.setCreatedAt(createdAt);

        // When
        MeetingRequestResponseDto dto = meetingRequestMapper.toDto(meetingRequest);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(requestId);
        assertThat(dto.requesterId()).isEqualTo(requesterId);
        assertThat(dto.receiverId()).isEqualTo(receiverId);
        assertThat(dto.startTime()).isEqualTo(startTime);
        assertThat(dto.endTime()).isEqualTo(endTime);
        assertThat(dto.title()).isEqualTo("Spotkanie projektowe");
        assertThat(dto.status()).isEqualTo("PENDING");
        assertThat(dto.createdAt()).isEqualTo(createdAt);
    }
}