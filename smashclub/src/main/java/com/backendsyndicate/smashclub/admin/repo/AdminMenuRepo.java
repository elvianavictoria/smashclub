package com.backendsyndicate.smashclub.admin.repo;

import com.backendsyndicate.smashclub.admin.model.AdminMenu;
import com.backendsyndicate.smashclub.admin.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminMenuRepo extends JpaRepository<AdminMenu, Integer> {

}
