package com.scheduler.backend.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        String email,
        String inviteCode,
        OffsetDateTime createdAt
) {}