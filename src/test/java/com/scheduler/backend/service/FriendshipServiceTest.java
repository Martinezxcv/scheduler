package com.scheduler.backend.service;

import com.scheduler.backend.dto.response.FriendshipResponseDto;
import com.scheduler.backend.entity.Friendship;
import com.scheduler.backend.entity.User;
import com.scheduler.backend.mapper.FriendshipMapper;
import com.scheduler.backend.repository.FriendshipRepository;
import com.scheduler.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendshipMapper friendshipMapper;

    @InjectMocks
    private FriendshipService friendshipService;

    private User currentUser;
    private User friendUser;
    private UUID currentUserId;
    private UUID friendId;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        friendId = UUID.randomUUID();

        currentUser = new User();
        currentUser.setId(currentUserId);
        currentUser.setEmail("test@example.com");
        currentUser.setUsername("testuser");

        friendUser = new User();
        friendUser.setId(friendId);
        friendUser.setEmail("friend@example.com");
        friendUser.setUsername("frienduser");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(currentUser.getEmail(), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void sendFriendRequest_Success() {
        when(userRepository.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(friendId)).thenReturn(Optional.of(friendUser));
        when(friendshipRepository.existsBetween(currentUser, friendUser)).thenReturn(false);

        Friendship savedFriendship = new Friendship();
        savedFriendship.setId(UUID.randomUUID());
        savedFriendship.setUser(currentUser);
        savedFriendship.setFriend(friendUser);
        savedFriendship.setStatus("PENDING");

        when(friendshipRepository.save(any(Friendship.class))).thenReturn(savedFriendship);
        when(friendshipMapper.toDto(savedFriendship)).thenReturn(
                new FriendshipResponseDto(savedFriendship.getId(), null, null, "PENDING", OffsetDateTime.now())
        );

        FriendshipResponseDto result = friendshipService.sendFriendRequest(friendId);

        assertNotNull(result);
        assertEquals("PENDING", result.status());
        verify(friendshipRepository, times(1)).save(any(Friendship.class));
    }

    @Test
    void sendFriendRequest_SelfAdd_ThrowsException() {
        when(userRepository.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> friendshipService.sendFriendRequest(currentUserId)
        );

        assertEquals("Nie możesz dodać samego siebie do znajomych.", exception.getMessage());
        verify(friendshipRepository, never()).save(any());
    }

    @Test
    void acceptFriendRequest_Success() {
        UUID friendshipId = UUID.randomUUID();
        Friendship friendship = new Friendship();
        friendship.setId(friendshipId);
        friendship.setUser(friendUser);
        friendship.setFriend(currentUser);
        friendship.setStatus("PENDING");

        when(userRepository.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(friendshipRepository.findById(friendshipId)).thenReturn(Optional.of(friendship));
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);
        when(friendshipMapper.toDto(friendship)).thenReturn(
                new FriendshipResponseDto(friendshipId, null, null, "ACCEPTED", OffsetDateTime.now())
        );

        FriendshipResponseDto result = friendshipService.acceptFriendRequest(friendshipId);

        assertNotNull(result);
        assertEquals("ACCEPTED", friendship.getStatus());
        verify(friendshipRepository, times(1)).save(friendship);
    }
}