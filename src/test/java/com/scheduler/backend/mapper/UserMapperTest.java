package com.scheduler.backend.mapper;

import com.scheduler.backend.dto.response.UserResponseDto;
import com.scheduler.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void shouldMapUserToUserResponseDto() {
        // Given
        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        User user = new User();
        user.setId(userId);
        user.setUsername("jan_kowalski");
        user.setEmail("jan@example.com");
        user.setPasswordHash("secret_hash");
        user.setInviteCode("INV123");
        user.setCreatedAt(now);

        // When
        UserResponseDto dto = userMapper.toDto(user);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(userId);
        assertThat(dto.username()).isEqualTo("jan_kowalski");
        assertThat(dto.email()).isEqualTo("jan@example.com");
        assertThat(dto.inviteCode()).isEqualTo("INV123");
        assertThat(dto.createdAt()).isEqualTo(now);
    }
}