package com.backendsyndicate.smashclub.auth.repository;

import com.backendsyndicate.smashclub.auth.model.PasswordResetTokens;
import com.backendsyndicate.smashclub.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokens, String> {

    // Existing methods
    Optional<PasswordResetTokens> findByTokenAndUsedAtIsNull(String token);
    Optional<PasswordResetTokens> findByToken(String token);

    @Modifying
    @Transactional
    @Query(value = "UPDATE password_reset_tokens SET UsedAt = :usedAt WHERE UserId = :userId AND UsedAt IS NULL",
            nativeQuery = true)
    int invalidateUserTokens(@Param("userId") String userId, @Param("usedAt") LocalDateTime usedAt);

    @Query("SELECT COUNT(p) FROM PasswordResetTokens p WHERE p.user = :user AND p.usedAt IS NULL")
    Long countByUserAndUsedAtIsNull(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetTokens p SET p.usedAt = :usedAt WHERE p.user = :user AND p.usedAt IS NULL")
    int invalidateByUser(@Param("user") User user, @Param("usedAt") LocalDateTime usedAt);

    @Query("SELECT p FROM PasswordResetTokens p WHERE p.user = :user")
    List<PasswordResetTokens> findByUser(@Param("user") User user);
}