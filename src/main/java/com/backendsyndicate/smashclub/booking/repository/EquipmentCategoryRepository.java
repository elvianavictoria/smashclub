package com.backendsyndicate.smashclub.booking.repository;

import com.backendsyndicate.smashclub.booking.model.EquipmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EquipmentCategoryRepository extends JpaRepository<EquipmentCategory, Long> {
    Optional<EquipmentCategory> findByCategoryName(String categoryName);
}