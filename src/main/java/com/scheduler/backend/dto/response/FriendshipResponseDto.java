package com.scheduler.backend.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FriendshipResponseDto(
        UUID id,
        UserResponseDto user,
        UserResponseDto friend,
        String status,
        OffsetDateTime createdAt
) {}