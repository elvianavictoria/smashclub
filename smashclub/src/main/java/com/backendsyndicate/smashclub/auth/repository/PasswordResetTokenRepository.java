package com.backendsyndicate.smashclub.auth.repository;

import com.backendsyndicate.smashclub.auth.model.PasswordResetTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokens, String> {
    Optional<PasswordResetTokens> findByTokenAndUsedAtIsNull(String token);

    @Modifying
    @Query("UPDATE PasswordResetToken p SET p.usedDate = :usedDate WHERE p.userId = :userId AND p.usedDate IS NULL")
    void invalidateUserTokens(@Param("userId") String userId, @Param("usedDate") LocalDateTime usedDate);

    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiredDate < :cutoffDate")
    void cleanupExpiredTokens(@Param("cutoffDate") LocalDateTime cutoffDate);
}