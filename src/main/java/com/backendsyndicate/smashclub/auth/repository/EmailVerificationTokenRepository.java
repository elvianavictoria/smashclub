package com.backendsyndicate.smashclub.auth.repository;

import com.backendsyndicate.smashclub.auth.model.EmailVerificationTokens;
import com.backendsyndicate.smashclub.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends
        JpaRepository<EmailVerificationTokens, String>,
        EmailVerificationTokenRepositoryCustom {

    @Query("SELECT t FROM EmailVerificationTokens t WHERE t.token = :token " +
            "AND t.usedAt IS NULL")
    Optional<EmailVerificationTokens> findByTokenAndUsedAtIsNull(@Param("token") String token);

    @Query("SELECT t FROM EmailVerificationTokens t WHERE t.token = :token " +
            "AND t.usedAt IS NOT NULL")
    Optional<EmailVerificationTokens> findByTokenAndUsedAtIsNotNull(@Param("token") String token);

    Optional<EmailVerificationTokens> findTopByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT t FROM EmailVerificationTokens t WHERE t.user.id = :userId " +
            "AND t.usedAt IS NULL " +
            "AND t.expiresAt > CURRENT_TIMESTAMP")
    Optional<EmailVerificationTokens> findValidByUserId(@Param("userId") String userId);
}