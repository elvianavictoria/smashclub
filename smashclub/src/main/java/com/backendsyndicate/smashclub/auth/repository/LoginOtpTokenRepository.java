package com.backendsyndicate.smashclub.auth.repository;

import com.backendsyndicate.smashclub.auth.model.LoginOtpTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LoginOtpTokenRepository extends JpaRepository<LoginOtpTokens, String> {
    Optional<LoginOtpTokens> findByUserIdAndOtpCodeAndUsedAtIsNull(
            @Param("userId") String userId,
            @Param("otpCode") String otpCode);

    @Modifying
    @Query("UPDATE LoginOtpTokens l SET l.usedDate = :usedDate WHERE l.userId = :userId AND l.usedDate IS NULL")
    void invalidateUserTokens(@Param("userId") String userId, @Param("usedDate") LocalDateTime usedDate);

    @Modifying
    @Query("DELETE FROM LoginOtpTokens l WHERE l.expiredDate < :cutoffDate")
    void cleanupExpiredTokens(@Param("cutoffDate") LocalDateTime cutoffDate);
}