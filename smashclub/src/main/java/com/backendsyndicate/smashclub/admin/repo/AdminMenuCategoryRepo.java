package com.backendsyndicate.smashclub.admin.repo;

import com.backendsyndicate.smashclub.admin.model.AdminMenu;
import com.backendsyndicate.smashclub.admin.model.AdminMenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminMenuCategoryRepo extends JpaRepository<AdminMenuCategory, Integer> {

}
