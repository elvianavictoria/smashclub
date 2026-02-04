package com.backendsyndicate.smashclub.auth.repository;

import com.backendsyndicate.smashclub.auth.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    // cek apakah email sudah ada
    boolean existsByEmail(String email);

    // cari user berdasarkan email
    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempt = u.failedLoginAttempt + 1 WHERE u.id = :userId")
    void incrementFailedAttempt(@Param("userId") String userId);

    Page<User> findAllByFullNameContainsOrEmailContains(String fullName, String email, Pageable pageable);
}