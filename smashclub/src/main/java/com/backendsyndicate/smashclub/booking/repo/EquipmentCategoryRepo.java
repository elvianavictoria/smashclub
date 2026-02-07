package com.backendsyndicate.smashclub.booking.repo;

import com.backendsyndicate.smashclub.booking.model.EquipmentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentCategoryRepo extends JpaRepository<EquipmentCategory, Long> {
    Page<EquipmentCategory> findAllByCategoryNameContainsIgnoreCase(String equipmentName, Pageable pageable);
}
