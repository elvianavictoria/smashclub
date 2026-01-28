package com.backendsyndicate.smashclub.auth.repository;

import com.backendsyndicate.smashclub.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    // cek apakah email sudah ada
    boolean existsByEmail(String email);

    // cari user berdasarkan email
    Optional<User> findByEmail(String email);
}