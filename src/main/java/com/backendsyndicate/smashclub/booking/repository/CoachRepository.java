package com.backendsyndicate.smashclub.booking.repository;

import com.backendsyndicate.smashclub.booking.model.Coach;
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
public interface CoachRepository extends JpaRepository<Coach, Long> {

    List<Coach> findByStatus(Byte status);

    @Query("SELECT c FROM Coach c WHERE c.status = " + BookingConstant.RESOURCE_ACTIVE)
    List<Coach> findAvailableCoaches();

    Optional<Coach> findByCoachCode(String coachCode);

    @Query(value = "SELECT c.* FROM smashclub.Coach c " +
            "WHERE c.Status = " + BookingConstant.RESOURCE_ACTIVE + " " +
            "AND c.ID NOT IN (" +
            "   SELECT cd.CoachID FROM smashclub.CoachDetail cd " +
            "   JOIN smashclub.Booking b ON cd.BookingID = b.ID " +
            "   WHERE cd.BookingDate = :date " +
            "   AND b.Status IN (1, 2) " +
            "   AND ((CAST(cd.StartTime AS TIME) <= CAST(:endTime AS TIME) " +
            "   AND CAST(cd.EndTime AS TIME) >= CAST(:startTime AS TIME)))" +
            ")",
            nativeQuery = true)
    List<Coach> findAvailableCoachesByDateTime(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    @Query(value = "SELECT CAST(CASE WHEN COUNT(c.ID) > 0 THEN 1 ELSE 0 END AS BIT) " +
            "FROM smashclub.Coach c " +
            "WHERE c.ID = :coachId " +
            "AND c.Status = " + BookingConstant.RESOURCE_ACTIVE + " " +
            "AND c.ID NOT IN (" +
            "   SELECT cd.CoachID FROM smashclub.CoachDetail cd " +
            "   JOIN smashclub.Booking b ON cd.BookingID = b.ID " +
            "   WHERE cd.CoachID = :coachId " +
            "   AND cd.BookingDate = :date " +
            "   AND b.Status IN (1, 2) " +
            "   AND ((CAST(cd.StartTime AS TIME) <= CAST(:endTime AS TIME) " +
            "   AND CAST(cd.EndTime AS TIME) >= CAST(:startTime AS TIME)))" +
            ")",
            nativeQuery = true)
    boolean isCoachAvailable(
            @Param("coachId") Long coachId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
}