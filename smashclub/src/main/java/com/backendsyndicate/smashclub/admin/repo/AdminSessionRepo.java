package com.backendsyndicate.smashclub.admin.repo;

import com.backendsyndicate.smashclub.admin.model.AdminSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminSessionRepo extends JpaRepository<AdminSession, Long> {
    List<AdminSession> findAllByAdminUser_Id(long userId);
    int countByAdminUser_IdAndStatus(long userId, int status);
    Optional<AdminSession> findByLoginToken(String loginToken);
    int countByLoginTokenAndStatus(String loginToken, int status);
}
