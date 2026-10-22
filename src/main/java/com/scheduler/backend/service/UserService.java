package com.scheduler.backend.service;

import com.scheduler.backend.dto.response.UserResponseDto;
import com.scheduler.backend.entity.User;
import com.scheduler.backend.mapper.UserMapper;
import com.scheduler.backend.repository.UserRepository;
import com.scheduler.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponseDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik o ID " + id + " nie został znaleziony"));
        return userMapper.toDto(user);
    }

    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Użytkownik o e-mailu " + email + " nie został znaleziony"));
        return userMapper.toDto(user);
    }

    public UserResponseDto getUserByInviteCode(String inviteCode) {
        User user = userRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new RuntimeException("Użytkownik z kodem zaproszenia " + inviteCode + " nie został znaleziony"));
        return userMapper.toDto(user);
    }

    public UserResponseDto registerUser(String username, String email, String rawPassword) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Adres e-mail jest już używany: " + email);
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Nazwa użytkownika jest już zajęta: " + username);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setInviteCode(generateUniqueInviteCode());
        user.setCreatedAt(OffsetDateTime.now());

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public String loginUser(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nieprawidłowy e-mail lub hasło"));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Nieprawidłowy e-mail lub hasło");
        }

        return jwtService.generateToken(user.getEmail());
    }

    private String generateUniqueInviteCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}