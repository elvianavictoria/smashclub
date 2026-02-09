package com.backendsyndicate.smashclub.admin.repo;

import com.backendsyndicate.smashclub.admin.model.AdminPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminPermissionRepo extends JpaRepository<AdminPermission, Integer> {
    List<AdminPermission> findAllByStatus(int status);
}
