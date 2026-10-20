package com.scheduler.backend.mapper;

import com.scheduler.backend.dto.response.FriendshipResponseDto;
import com.scheduler.backend.entity.Friendship;
import com.scheduler.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FriendshipMapperTest {

    @Autowired
    private FriendshipMapper friendshipMapper;

    @Test
    void shouldMapFriendshipToFriendshipResponseDto() {
        // Given
        UUID friendshipId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("jan");
        user.setEmail("jan@example.com");
        user.setCreatedAt(now);

        User friend = new User();
        friend.setId(UUID.randomUUID());
        friend.setUsername("anna");
        friend.setEmail("anna@example.com");
        friend.setCreatedAt(now);

        Friendship friendship = new Friendship();
        friendship.setId(friendshipId);
        friendship.setUser(user);
        friendship.setFriend(friend);
        friendship.setStatus("ACCEPTED");
        friendship.setCreatedAt(now);

        // When
        FriendshipResponseDto dto = friendshipMapper.toDto(friendship);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(friendshipId);
        assertThat(dto.status()).isEqualTo("ACCEPTED");
        assertThat(dto.createdAt()).isEqualTo(now);

        assertThat(dto.user()).isNotNull();
        assertThat(dto.user().username()).isEqualTo("jan");

        assertThat(dto.friend()).isNotNull();
        assertThat(dto.friend().username()).isEqualTo("anna");
    }
}