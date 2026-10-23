package com.scheduler.backend.repository;

import com.scheduler.backend.entity.Friendship;
import com.scheduler.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {

    @Query("SELECT f FROM Friendship f WHERE f.user = :user OR f.friend = :user")
    List<Friendship> findAllByUserInvolved(@Param("user") User user);

    @Query("SELECT f FROM Friendship f WHERE (f.user = :user1 AND f.friend = :user2) OR (f.user = :user2 AND f.friend = :user1)")
    Optional<Friendship> findFriendshipBetween(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE (f.user = :user1 AND f.friend = :user2) OR (f.user = :user2 AND f.friend = :user1)")
    boolean existsBetween(@Param("user1") User user1, @Param("user2") User user2);
}