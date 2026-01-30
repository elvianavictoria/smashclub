package com.backendsyndicate.smashclub.auth.repository;

import com.backendsyndicate.smashclub.auth.model.EmailVerificationTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationTokens, String> {
    Optional<EmailVerificationTokens> findByTokenAndUsedAtIsNull(String token);
    List<EmailVerificationTokens> findByUserId(String userId);

    @Modifying
    @Query("UPDATE EmailVerificationToken e SET e.usedDate = :usedDate WHERE e.userId = :userId AND e.usedDate IS NULL")
    void invalidateUserTokens(@Param("userId") String userId, @Param("usedDate") LocalDateTime usedDate);

    @Modifying
    @Query("DELETE FROM EmailVerificationToken e WHERE e.expiredDate < :cutoffDate")
    void cleanupExpiredTokens(@Param("cutoffDate") LocalDateTime cutoffDate);
}