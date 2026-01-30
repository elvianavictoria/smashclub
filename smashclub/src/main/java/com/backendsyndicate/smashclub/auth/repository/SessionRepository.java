package com.backendsyndicate.smashclub.auth.repository;

import com.backendsyndicate.smashclub.auth.model.Sessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Sessions, String> {
    Optional<Sessions> findBySessionTokenAndExpiresAtAfter(
            @Param("sessionToken") String sessionToken,
            @Param("expiredDate") LocalDateTime expiredDate);

    @Modifying
    @Query("UPDATE Session s SET s.expiredDate = :expiredDate WHERE s.userId = :userId AND s.expiredDate > CURRENT_TIMESTAMP")
    void invalidateUserSessions(@Param("userId") String userId, @Param("expiredDate") LocalDateTime expiredDate);

    @Modifying
    @Query("UPDATE Session s SET s.expiredDate = :expiredDate WHERE s.sessionToken = :sessionToken AND s.expiredDate > CURRENT_TIMESTAMP")
    void invalidateSession(@Param("sessionToken") String sessionToken, @Param("expiredDate") LocalDateTime expiredDate);

    @Modifying
    @Query("DELETE FROM Session s WHERE s.expiredDate < :cutoffDate")
    void cleanupExpiredSessions(@Param("cutoffDate") LocalDateTime cutoffDate);
}