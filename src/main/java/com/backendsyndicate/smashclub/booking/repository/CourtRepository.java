package com.backendsyndicate.smashclub.booking.repository;

import com.backendsyndicate.smashclub.booking.model.Court;
import com.backendsyndicate.smashclub.common.constant.BookingConstant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {

    List<Court> findByStatus(Byte status);

    @Query("SELECT c FROM Court c WHERE c.status = " + BookingConstant.RESOURCE_ACTIVE)
    List<Court> findAvailableCourts();

    boolean existsByCourtCode(String courtCode);

    // Untuk seeder
    Optional<Court> findByCourtCode(String courtCode);
}