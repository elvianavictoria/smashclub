package com.backendsyndicate.smashclub.auth.repository.impl;

import com.backendsyndicate.smashclub.auth.model.Sessions;
import com.backendsyndicate.smashclub.auth.repository.SessionRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
public class SessionRepositoryImpl implements SessionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void invalidateUserTokens(String userId, LocalDateTime now) {
        String jpql = "UPDATE Sessions s SET s.invalidatedAt = :now " +
                "WHERE s.user.id = :userId " +
                "AND s.invalidatedAt IS NULL";

        Query query = entityManager.createQuery(jpql)
                .setParameter("now", now)
                .setParameter("userId", userId);

        int updated = query.executeUpdate();
        log.info("Invalidated {} sessions for user: {}", updated, userId);
    }

    @Override
    @Transactional
    public void invalidateUserTokensExcept(String userId, String exceptToken, LocalDateTime now) {
        String jpql = "UPDATE Sessions s SET s.invalidatedAt = :now " +
                "WHERE s.user.id = :userId " +
                "AND s.sessionToken != :exceptToken " +
                "AND s.invalidatedAt IS NULL";

        Query query = entityManager.createQuery(jpql)
                .setParameter("now", now)
                .setParameter("userId", userId)
                .setParameter("exceptToken", exceptToken);

        int updated = query.executeUpdate();
        log.info("Invalidated {} sessions (except current) for user: {}", updated, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByUserIdAndTokenTypeAndExpiresAtAfterAndInvalidatedAtIsNull(
            String userId, String tokenType, LocalDateTime now) {

        String jpql = "SELECT COUNT(s) FROM Sessions s WHERE s.user.id = :userId " +
                "AND s.tokenType = :tokenType " +
                "AND s.expiresAt > :now " +
                "AND s.invalidatedAt IS NULL";

        Query query = entityManager.createQuery(jpql)
                .setParameter("userId", userId)
                .setParameter("tokenType", tokenType)
                .setParameter("now", now);

        return (long) query.getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sessions> findByUserIdAndTokenTypeAndExpiresAtAfterAndInvalidatedAtIsNull(
            String userId, String tokenType, LocalDateTime now) {

        String jpql = "SELECT s FROM Sessions s WHERE s.user.id = :userId " +
                "AND s.tokenType = :tokenType " +
                "AND s.expiresAt > :now " +
                "AND s.invalidatedAt IS NULL " +
                "ORDER BY s.createdAt DESC";

        Query query = entityManager.createQuery(jpql, Sessions.class)
                .setParameter("userId", userId)
                .setParameter("tokenType", tokenType)
                .setParameter("now", now);

        return query.getResultList();
    }
}