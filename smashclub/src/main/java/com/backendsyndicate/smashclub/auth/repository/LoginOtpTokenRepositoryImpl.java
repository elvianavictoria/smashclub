package com.backendsyndicate.smashclub.auth.repository;

import com.backendsyndicate.smashclub.auth.repository.LoginOtpTokenRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
@Slf4j
public class LoginOtpTokenRepositoryImpl implements LoginOtpTokenRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void invalidateUserTokens(String userId, LocalDateTime now) {
        String jpql = "UPDATE LoginOtpTokens o SET o.usedAt = :now " +
                "WHERE o.user.id = :userId " +
                "AND o.usedAt IS NULL";

        Query query = entityManager.createQuery(jpql)
                .setParameter("now", now)
                .setParameter("userId", userId);

        int updated = query.executeUpdate();
        log.info("Invalidated {} OTP tokens for user: {}", updated, userId);
    }
}