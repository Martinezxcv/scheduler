package com.scheduler.backend.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public record FriendshipCreateRequestDto(
        @NotNull UUID friendId
) {}