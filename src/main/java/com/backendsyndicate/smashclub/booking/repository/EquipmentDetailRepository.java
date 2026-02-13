package com.backendsyndicate.smashclub.booking.repository;

import com.backendsyndicate.smashclub.booking.model.EquipmentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface EquipmentDetailRepository extends JpaRepository<EquipmentDetail, Long> {

    // Query spesifik untuk menghindari N+1
    @Query("SELECT ed FROM EquipmentDetail ed " +
            "LEFT JOIN FETCH ed.equipment " +
            "LEFT JOIN FETCH ed.equipment.equipmentCategory " +
            "LEFT JOIN FETCH ed.booking " +
            "WHERE ed.booking.id = :bookingId")
    List<EquipmentDetail> findByBookingIdWithDetails(@Param("bookingId") Long bookingId);

    @Query("SELECT ed FROM EquipmentDetail ed " +
            "LEFT JOIN FETCH ed.equipment " +
            "LEFT JOIN FETCH ed.equipment.equipmentCategory " +
            "LEFT JOIN FETCH ed.booking " +
            "WHERE ed.booking.bookingCode = :bookingCode")
    List<EquipmentDetail> findByBookingCodeWithDetails(@Param("bookingCode") String bookingCode);

    // Query untuk menghitung total quantity equipment yang dipinjam pada periode tertentu
    @Query(value = "SELECT COALESCE(SUM(ed.Quantity), 0) FROM smashclub.EquipmentDetail ed " +
            "JOIN smashclub.Booking b ON ed.BookingID = b.ID " +
            "WHERE ed.Equipment = :equipmentId " +
            "AND b.BookingDate = :date " +
            "AND b.Status IN (1, 2) " +
            "AND ((CAST(ed.StartTime AS TIME) <= CAST(:endTime AS TIME) " +
            "AND CAST(ed.EndTime AS TIME) >= CAST(:startTime AS TIME)))",
            nativeQuery = true)
    int getTotalBookedQuantity(
            @Param("equipmentId") Long equipmentId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
}