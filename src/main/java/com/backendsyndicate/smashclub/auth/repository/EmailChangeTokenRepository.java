package com.backendsyndicate.smashclub.auth.repository;

import com.backendsyndicate.smashclub.auth.model.EmailChangeToken;
import com.backendsyndicate.smashclub.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailChangeTokenRepository extends JpaRepository<EmailChangeToken, String> {

    Optional<EmailChangeToken> findByTokenAndUsedAtIsNull(String token);

    Optional<EmailChangeToken> findByUserIdAndUsedAtIsNull(String userId);

    Optional<EmailChangeToken> findTopByUserOrderByCreatedAtDesc(User user);

    @Modifying
    @Query("UPDATE EmailChangeToken e SET e.usedAt = :usedAt WHERE e.user.id = :userId AND e.usedAt IS NULL")
    int invalidateUserTokens(@Param("userId") String userId, @Param("usedAt") LocalDateTime usedAt);

    @Query("SELECT COUNT(e) FROM EmailChangeToken e WHERE e.user = :user AND e.usedAt IS NULL")
    Long countByUserAndUsedAtIsNull(@Param("user") User user);
}