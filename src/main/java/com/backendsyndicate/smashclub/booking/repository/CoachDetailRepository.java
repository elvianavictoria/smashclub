package com.backendsyndicate.smashclub.booking.repository;

import com.backendsyndicate.smashclub.booking.model.CoachDetail;
import com.backendsyndicate.smashclub.common.constant.BookingConstant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CoachDetailRepository extends JpaRepository<CoachDetail, Long> {

    @Query(value = "SELECT cd.* FROM smashclub.CoachDetail cd " +
            "JOIN smashclub.Booking b ON cd.BookingID = b.ID " +
            "WHERE cd.CoachID = :coachId " +
            "AND cd.BookingDate = :date " +
            "AND b.Status IN (1, 2) " +
            "AND ((CAST(cd.StartTime AS TIME) <= CAST(:endTime AS TIME) " +
            "AND CAST(cd.EndTime AS TIME) >= CAST(:startTime AS TIME)))",
            nativeQuery = true)
    List<CoachDetail> findCoachBookings(
            @Param("coachId") Long coachId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    // Query spesifik untuk menghindari N+1
    @Query("SELECT cd FROM CoachDetail cd " +
            "LEFT JOIN FETCH cd.coach " +
            "LEFT JOIN FETCH cd.booking " +
            "WHERE cd.booking.id = :bookingId")
    List<CoachDetail> findByBookingIdWithDetails(@Param("bookingId") Long bookingId);

    @Query("SELECT cd FROM CoachDetail cd " +
            "LEFT JOIN FETCH cd.coach " +
            "LEFT JOIN FETCH cd.booking " +
            "WHERE cd.booking.bookingCode = :bookingCode")
    List<CoachDetail> findByBookingCodeWithDetails(@Param("bookingCode") String bookingCode);

    // Query untuk cek ketersediaan coach dengan overlapping time
    @Query("SELECT COUNT(cd) FROM CoachDetail cd " +
            "WHERE cd.coach.id = :coachId " +
            "AND cd.bookingDate = :date " +
            "AND cd.booking.status IN (" + BookingConstant.BOOKING_PENDING + ", " + BookingConstant.BOOKING_CONFIRMED + ") " +
            "AND ((cd.startTime <= :endTime AND cd.endTime >= :startTime))")
    int countOverlappingCoachBookings(
            @Param("coachId") Long coachId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    default List<CoachDetail> findCoachBookings(Long coachId, LocalDate date) {
        return findCoachBookings(
                coachId,
                date,
                LocalTime.of(0, 0),  // 00:00
                LocalTime.of(23, 59) // 23:59
        );
    }
}