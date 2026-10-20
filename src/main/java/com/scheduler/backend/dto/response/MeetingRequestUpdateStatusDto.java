package com.scheduler.backend.dto.response;

import jakarta.validation.constraints.NotBlank;

public record MeetingRequestUpdateStatusDto(
        @NotBlank String status
) {}