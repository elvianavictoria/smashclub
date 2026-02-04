package com.backendsyndicate.smashclub.booking.repo;

import com.backendsyndicate.smashclub.booking.model.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepo extends JpaRepository<Equipment, Long> {
    Page<Equipment> findAllByEquipmentNameContains(String equipmentName, Pageable pageable);
}
