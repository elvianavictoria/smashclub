package com.backendsyndicate.smashclub.booking.repository;

import com.backendsyndicate.smashclub.booking.model.Booking;
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
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT COUNT(b) FROM Booking b WHERE cast(b.createdAt as string) LIKE CONCAT(CURRENT_DATE, '%')")
    Long countTodayBooking();

    Optional<Booking> findByBookingCode(String bookingCode);

    List<Booking> findByUserId(String userId);

    @Query(value = "SELECT b.* FROM smashclub.Booking b " +
            "WHERE b.CourtID = :courtId " +
            "AND b.BookingDate = :date " +
            "AND b.Status IN (1, 2) " +
            "AND ((CAST(b.StartTime AS TIME) <= CAST(:endTime AS TIME) " +
            "AND CAST(b.EndTime AS TIME) >= CAST(:startTime AS TIME)))",
            nativeQuery = true)
    List<Booking> findOverlappingBookings(
            @Param("courtId") Long courtId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    @Query("SELECT b FROM Booking b WHERE b.court.id = :courtId " +
            "AND b.bookingDate = :date " +
            "AND b.status IN (" + BookingConstant.BOOKING_PENDING + ", " + BookingConstant.BOOKING_CONFIRMED + ")")
    List<Booking> findByCourtAndDate(
            @Param("courtId") Long courtId,
            @Param("date") LocalDate date);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId " +
            "AND b.status = " + BookingConstant.BOOKING_PENDING)
    long countPendingBookingsByUser(@Param("userId") String userId);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.user LEFT JOIN FETCH b.court WHERE b.id = :id")
    Optional<Booking> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.user LEFT JOIN FETCH b.court WHERE b.bookingCode = :bookingCode")
    Optional<Booking> findByBookingCodeWithDetails(@Param("bookingCode") String bookingCode);
}