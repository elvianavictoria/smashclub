package com.backendsyndicate.smashclub.admin.repo;

import com.backendsyndicate.smashclub.admin.model.AdminRole;
import com.backendsyndicate.smashclub.admin.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRoleRepo extends JpaRepository<AdminRole, Long> {
}
