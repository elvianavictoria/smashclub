package com.backendsyndicate.smashclub.booking.repository;

import com.backendsyndicate.smashclub.booking.model.Equipment;
import com.backendsyndicate.smashclub.common.constant.BookingConstant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByEquipmentCategoryId(Long categoryId);
    List<Equipment> findByStatus(Byte status);
    Optional<Equipment> findByEquipmentNameAndBrand(String equipmentName, String brand);

    @Query("SELECT e FROM Equipment e WHERE e.status = " + BookingConstant.RESOURCE_ACTIVE)
    List<Equipment> findAvailableEquipment();

    @Query(value = "SELECT e.* FROM smashclub.Equipment e " +
            "WHERE e.Status = " + BookingConstant.RESOURCE_ACTIVE + " " +
            "AND e.Stock > 0 " +
            "AND (e.Stock - COALESCE((" +
            "   SELECT SUM(ed.Quantity) FROM smashclub.EquipmentDetail ed " +
            "   JOIN smashclub.Booking b ON ed.BookingID = b.ID " +
            "   WHERE ed.Equipment = e.ID " +
            "   AND b.BookingDate = :date " +
            "   AND b.Status IN (1, 2) " +
            "   AND ((CAST(ed.StartTime AS TIME) <= CAST(:endTime AS TIME) " +
            "   AND CAST(ed.EndTime AS TIME) >= CAST(:startTime AS TIME)))" +
            "), 0)) >= :requiredQuantity",
            nativeQuery = true)
    List<Equipment> findAvailableEquipmentWithStockByDateTime(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("requiredQuantity") Integer requiredQuantity);

    @Query(value = "SELECT e.Stock - COALESCE((" +
            "   SELECT SUM(ed.Quantity) FROM smashclub.EquipmentDetail ed " +
            "   JOIN smashclub.Booking b ON ed.BookingID = b.ID " +
            "   WHERE ed.Equipment = :equipmentId " +
            "   AND b.BookingDate = :date " +
            "   AND b.Status IN (1, 2) " +
            "   AND ((CAST(ed.StartTime AS TIME) <= CAST(:endTime AS TIME) " +
            "   AND CAST(ed.EndTime AS TIME) >= CAST(:startTime AS TIME)))" +
            "), 0) FROM smashclub.Equipment e WHERE e.ID = :equipmentId",
            nativeQuery = true)
    int getAvailableStock(
            @Param("equipmentId") Long equipmentId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    // Method overloading untuk backward compatibility
    default List<Equipment> findAvailableEquipmentWithStock(Integer requiredQuantity) {
        return findAvailableEquipmentWithStockByDateTime(
                LocalDate.now(),
                LocalTime.MIN,
                LocalTime.MAX,
                requiredQuantity != null ? requiredQuantity : 1);
    }
}