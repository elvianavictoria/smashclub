package com.backendsyndicate.smashclub.auth.repository;

import com.backendsyndicate.smashclub.auth.model.LoginOtpTokens;
import com.backendsyndicate.smashclub.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LoginOtpTokenRepository extends JpaRepository<LoginOtpTokens, String>, LoginOtpTokenRepositoryCustom {

    @Query("SELECT o FROM LoginOtpTokens o WHERE o.user.id = :userId " +
            "AND o.otpCode = :otpCode " +
            "AND o.usedAt IS NULL")
    Optional<LoginOtpTokens> findByUserIdAndOtpCodeAndUsedAtIsNull(
            @Param("userId") String userId,
            @Param("otpCode") String otpCode
    );

    @Query("SELECT o FROM LoginOtpTokens o WHERE o.user.id = :userId " +
            "AND o.otpCode = :otpCode " +
            "AND o.usedAt IS NOT NULL")
    Optional<LoginOtpTokens> findByUserIdAndOtpCodeAndUsedAtIsNotNull(
            @Param("userId") String userId,
            @Param("otpCode") String otpCode
    );

    Optional<LoginOtpTokens> findTopByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT o FROM LoginOtpTokens o WHERE o.user.id = :userId " +
            "AND o.expiresAt > :now " +
            "AND o.usedAt IS NULL")
    Optional<LoginOtpTokens> findValidByUserId(
            @Param("userId") String userId,
            @Param("now") LocalDateTime now
    );
}