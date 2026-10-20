package com.scheduler.backend.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MeetingRequestResponseDto(
        UUID id,
        UUID requesterId,
        UUID receiverId,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        String title,
        String status,
        OffsetDateTime createdAt
) {}