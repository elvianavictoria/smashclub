package com.backendsyndicate.smashclub.auth.repository;

import com.backendsyndicate.smashclub.auth.model.Sessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Sessions, String>, SessionRepositoryCustom {

    Optional<Sessions> findBySessionToken(String sessionToken);

    @Query("SELECT s FROM Sessions s WHERE s.sessionToken = :sessionToken " +
            "AND s.tokenType = :tokenType " +
            "AND s.expiresAt > :now " +
            "AND s.invalidatedAt IS NULL")
    Optional<Sessions> findBySessionTokenAndTokenTypeAndExpiresAtAfterAndInvalidatedAtIsNull(
            @Param("sessionToken") String sessionToken,
            @Param("tokenType") String tokenType,
            @Param("now") LocalDateTime now
    );

    @Query("SELECT s FROM Sessions s WHERE s.user.id = :userId " +
            "AND s.tokenType = :tokenType " +
            "AND s.expiresAt > :now " +
            "AND s.invalidatedAt IS NULL")
    List<Sessions> findByUserIdAndTokenTypeAndExpiresAtAfterAndInvalidatedAtIsNull(
            @Param("userId") String userId,
            @Param("tokenType") String tokenType,
            @Param("now") LocalDateTime now
    );

    @Query("SELECT COUNT(s) FROM Sessions s WHERE s.user.id = :userId " +
            "AND s.tokenType = :tokenType " +
            "AND s.expiresAt > :now " +
            "AND s.invalidatedAt IS NULL")
    long countByUserIdAndTokenTypeAndExpiresAtAfterAndInvalidatedAtIsNull(
            @Param("userId") String userId,
            @Param("tokenType") String tokenType,
            @Param("now") LocalDateTime now
    );

    List<Sessions> findByUserId(String userId);

    List<Sessions> findByUserIdAndTokenType(String userId, String tokenType);
}