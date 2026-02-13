package com.backendsyndicate.smashclub.admin.repo;

import com.backendsyndicate.smashclub.admin.model.AdminUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminUserRepo extends JpaRepository<AdminUser, Long> {
    Page<AdminUser> findAllByUsernameContainsOrFullNameContainsIgnoreCaseAndStatus(String username, String fullname, int status, Pageable pageable);
    Page<AdminUser> findAllByUsernameContainsOrFullNameContainsIgnoreCase(String username, String fullname, Pageable pageable);
    Page<AdminUser> findAllByStatus(int status, Pageable pageable);

    Optional<AdminUser> findByUsername(String username);
    Optional<AdminUser> findByUsernameAndStatus(String username, int status);
}
