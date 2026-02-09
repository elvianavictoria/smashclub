package com.backendsyndicate.smashclub.admin.repo;

import com.backendsyndicate.smashclub.admin.model.AdminRole;
import com.backendsyndicate.smashclub.admin.model.AdminUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRoleRepo extends JpaRepository<AdminRole, Integer> {
    Page<AdminRole> findAllByRoleCodeContainsOrRoleNameContainsIgnoreCaseAndStatus(String roleCode, String roleName, byte status, Pageable pageable);
    Page<AdminRole> findAllByRoleCodeContainsOrRoleNameContainsIgnoreCase(String roleCode, String roleName, Pageable pageable);
    Page<AdminRole> findAllByStatus(byte status, Pageable pageable);
}
