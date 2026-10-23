package com.scheduler.backend.service;

import com.scheduler.backend.dto.response.FriendshipResponseDto;
import com.scheduler.backend.entity.Friendship;
import com.scheduler.backend.entity.User;
import com.scheduler.backend.mapper.FriendshipMapper;
import com.scheduler.backend.repository.FriendshipRepository;
import com.scheduler.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final FriendshipMapper friendshipMapper;

    private User getCurrentUser() {
        String email = (String) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono zalogowanego użytkownika."));
    }

    @Transactional
    public FriendshipResponseDto sendFriendRequest(UUID friendId) {
        User currentUser = getCurrentUser();

        if (currentUser.getId().equals(friendId)) {
            throw new IllegalArgumentException("Nie możesz dodać samego siebie do znajomych.");
        }

        User friendUser = userRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Docelowy użytkownik nie został znaleziony."));

        if (friendshipRepository.existsBetween(currentUser, friendUser)) {
            throw new IllegalStateException("Relacja lub zaproszenie między tymi użytkownikami już istnieje.");
        }

        Friendship friendship = new Friendship();
        friendship.setUser(currentUser);
        friendship.setFriend(friendUser);
        friendship.setStatus("PENDING");

        Friendship savedFriendship = friendshipRepository.save(friendship);
        return friendshipMapper.toDto(savedFriendship);
    }

    @Transactional
    public FriendshipResponseDto acceptFriendRequest(UUID friendshipId) {
        User currentUser = getCurrentUser();

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new EntityNotFoundException("Zaproszenie nie zostało znalezione."));

        if (!friendship.getFriend().getId().equals(currentUser.getId())) {
            throw new SecurityException("Nie masz uprawnień do zaakceptowania tego zaproszenia.");
        }

        friendship.setStatus("ACCEPTED");
        Friendship updatedFriendship = friendshipRepository.save(friendship);
        return friendshipMapper.toDto(updatedFriendship);
    }

    @Transactional
    public void removeFriendship(UUID otherUserId) {
        User currentUser = getCurrentUser();

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new EntityNotFoundException("Drugi użytkownik nie został znaleziony."));

        Friendship friendship = friendshipRepository.findFriendshipBetween(currentUser, otherUser)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono relacji między użytkownikami."));

        friendshipRepository.delete(friendship);
    }

    public List<FriendshipResponseDto> getFriendshipsForCurrentUser() {
        User currentUser = getCurrentUser();
        List<Friendship> friendships = friendshipRepository.findAllByUserInvolved(currentUser);

        return friendships.stream()
                .map(friendshipMapper::toDto)
                .collect(Collectors.toList());
    }
}