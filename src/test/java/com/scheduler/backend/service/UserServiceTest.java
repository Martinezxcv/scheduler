package com.scheduler.backend.service;

import com.scheduler.backend.dto.response.UserResponseDto;
import com.scheduler.backend.entity.User;
import com.scheduler.backend.mapper.UserMapper;
import com.scheduler.backend.repository.UserRepository;
import com.scheduler.backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldGetUserByIdSuccessfully() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("jan");

        UserResponseDto dto = new UserResponseDto(userId, "jan", "jan@example.com", "INV123", OffsetDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        // When
        UserResponseDto result = userService.getUserById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.username()).isEqualTo("jan");
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundById() {
        // Given
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nie został znaleziony");
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // Given
        String username = "nowy_user";
        String email = "nowy@example.com";
        String rawPassword = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn("hashedPassword123");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(UUID.randomUUID());
            u.setCreatedAt(OffsetDateTime.now());
            return u;
        });

        UserResponseDto expectedDto = new UserResponseDto(
                UUID.randomUUID(), username, email, "XYZ12345", OffsetDateTime.now()
        );
        when(userMapper.toDto(any(User.class))).thenReturn(expectedDto);

        // When
        UserResponseDto result = userService.registerUser(username, email, rawPassword);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(username);
        assertThat(result.email()).isEqualTo(email);

        verify(userRepository).findByEmail(email);
        verify(userRepository).findByUsername(username);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPasswordHash()).isEqualTo("hashedPassword123");
        assertThat(savedUser.getInviteCode()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExistsOnRegistration() {
        // Given
        String username = "user1";
        String email = "zajety@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        // When & Then
        assertThatThrownBy(() -> userService.registerUser(username, email, "pass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Adres e-mail jest już używany");

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExistsOnRegistration() {
        // Given
        String username = "zajety_user";
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        // When & Then
        assertThatThrownBy(() -> userService.registerUser(username, email, "pass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nazwa użytkownika jest już zajęta");

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldLoginUserSuccessfullyAndReturnToken() {
        // Given
        String email = "jan@example.com";
        String rawPassword = "password123";
        String hashedPassword = "hashedPassword123";
        String expectedToken = "mocked.jwt.token";

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(hashedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);
        when(jwtService.generateToken(email)).thenReturn(expectedToken);

        // When
        String token = userService.loginUser(email, rawPassword);

        // Then
        assertThat(token).isEqualTo(expectedToken);
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(rawPassword, hashedPassword);
        verify(jwtService).generateToken(email);
    }

    @Test
    void shouldThrowExceptionWhenLoginWithInvalidPassword() {
        // Given
        String email = "jan@example.com";
        String rawPassword = "wrongPassword";
        String hashedPassword = "hashedPassword123";

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(hashedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.loginUser(email, rawPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nieprawidłowy e-mail lub hasło");

        verify(jwtService, never()).generateToken(any());
    }
}