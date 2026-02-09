package com.backendsyndicate.smashclub.auth.repository;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LoginOtpTokenRepositoryCustom {
    void invalidateUserTokens(String userId, LocalDateTime now);
}