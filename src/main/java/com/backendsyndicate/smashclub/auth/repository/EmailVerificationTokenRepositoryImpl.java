package com.backendsyndicate.smashclub.auth.repository;

import com.backendsyndicate.smashclub.auth.repository.EmailVerificationTokenRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
@Slf4j
public class EmailVerificationTokenRepositoryImpl implements EmailVerificationTokenRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void invalidateUserTokens(String userId, LocalDateTime now) {
        String jpql = "UPDATE EmailVerificationTokens t SET t.usedAt = :now " +
                "WHERE t.user.id = :userId " +
                "AND t.usedAt IS NULL";

        Query query = entityManager.createQuery(jpql)
                .setParameter("now", now)
                .setParameter("userId", userId);

        int updated = query.executeUpdate();
        log.info("Invalidated {} email verification tokens for user: {}", updated, userId);
    }
}