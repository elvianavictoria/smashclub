package com.backendsyndicate.smashclub.booking.repository;

import com.backendsyndicate.smashclub.booking.model.EquipmentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EquipmentCategoryRepository extends JpaRepository<EquipmentCategory, Long> {
    Optional<EquipmentCategory> findByCategoryName(String categoryName);

    // Admin CMS
    Page<EquipmentCategory> findAllByCategoryNameContainsIgnoreCase(String equipmentName, Pageable pageable);
}