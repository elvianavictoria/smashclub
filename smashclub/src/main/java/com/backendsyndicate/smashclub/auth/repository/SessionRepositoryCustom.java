package com.backendsyndicate.smashclub.auth.repository;

import com.backendsyndicate.smashclub.auth.model.Sessions;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepositoryCustom {
    void invalidateUserTokens(String userId, LocalDateTime now);
    void invalidateUserTokensExcept(String userId, String exceptToken, LocalDateTime now);
    long countByUserIdAndTokenTypeAndExpiresAtAfterAndInvalidatedAtIsNull(
            String userId, String tokenType, LocalDateTime now);
    List<Sessions> findByUserIdAndTokenTypeAndExpiresAtAfterAndInvalidatedAtIsNull(
            String userId, String tokenType, LocalDateTime now);
}