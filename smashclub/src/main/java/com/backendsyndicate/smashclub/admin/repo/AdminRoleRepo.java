package com.backendsyndicate.smashclub.admin.repo;

import com.backendsyndicate.smashclub.admin.model.AdminRole;
import com.backendsyndicate.smashclub.admin.model.AdminUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRoleRepo extends JpaRepository<AdminRole, Integer> {
    Page<AdminRole> findAllByRoleCodeContainsOrRoleNameContains(String roleCode, String roleName, Pageable pageable);
}
