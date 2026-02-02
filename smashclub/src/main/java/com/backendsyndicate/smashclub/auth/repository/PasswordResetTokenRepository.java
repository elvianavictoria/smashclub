package com.backendsyndicate.smashclub.auth.repository;

import com.backendsyndicate.smashclub.auth.model.PasswordResetTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokens, String> {

    // 1. Cari token yang masih valid (belum dipakai)
    Optional<PasswordResetTokens> findByTokenAndUsedAtIsNull(String token);

    // 2. Cari token berdasarkan user ID
    Optional<PasswordResetTokens> findByUserIdAndUsedAtIsNull(String userId);

    // 3. INVALIDATE TOKENS - NATIVE QUERY (FIXED)
    @Modifying
    @Transactional
    @Query(value = "UPDATE password_reset_tokens SET used_at = :usedAt WHERE user_id = :userId AND used_at IS NULL",
            nativeQuery = true)
    void invalidateUserTokens(@Param("userId") String userId, @Param("usedAt") LocalDateTime usedAt);

    // 4. CLEANUP EXPIRED TOKENS - NATIVE QUERY (FIXED)
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM password_reset_tokens WHERE expires_at < :cutoffDate",
            nativeQuery = true)
    void cleanupExpiredTokens(@Param("cutoffDate") LocalDateTime cutoffDate);

    // 5. Cari token yang expired tapi belum dipakai
    @Query("SELECT p FROM PasswordResetTokens p WHERE p.expiresAt < :now AND p.usedAt IS NULL")
    java.util.List<PasswordResetTokens> findExpiredUnusedTokens(@Param("now") LocalDateTime now);
}