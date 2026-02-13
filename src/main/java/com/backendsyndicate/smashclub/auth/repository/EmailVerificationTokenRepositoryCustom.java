package com.backendsyndicate.smashclub.auth.repository;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EmailVerificationTokenRepositoryCustom {
    void invalidateUserTokens(String userId, LocalDateTime now);
}