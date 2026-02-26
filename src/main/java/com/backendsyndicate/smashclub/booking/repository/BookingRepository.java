package com.backendsyndicate.smashclub.booking.repository;

import com.backendsyndicate.smashclub.booking.model.Booking;
import com.backendsyndicate.smashclub.common.constant.BookingConstant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT COUNT(b) FROM Booking b WHERE cast(b.createdAt as string) LIKE CONCAT(:today, '%')")
    Long countTodayBooking(@Param("today") LocalDate today);

    Optional<Booking> findByBookingCode(String bookingCode);

    Page<Booking> findByUserId(String userId, Pageable pageable);

    List<Booking> findByUserId(String userId);

    @Query(value = "SELECT b.* FROM smashclub.Booking b " +
            "WHERE b.CourtID = :courtId " +
            "AND b.BookingDate = :date " +
            "AND b.Status IN (1, 2) " +
            "AND ((CAST(b.StartTime AS TIME) < CAST(:endTime AS TIME) " +
            "AND CAST(b.EndTime AS TIME) > CAST(:startTime AS TIME)))",
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

    // Statistic Related
    @Query(value="SELECT COUNT(b) FROM Booking b WHERE b.createdAt BETWEEN ?1 AND ?2 AND b.status > 0")
    int countByCreatedAt(LocalDateTime startDate, LocalDateTime endDate);
    @Query(value="SELECT AVG(b.durationHour) FROM Booking b WHERE b.createdAt BETWEEN ?1 AND ?2 AND status > 0")
    double averageBookingHourByCreatedAt(LocalDateTime startDate, LocalDateTime endDate);
    @Query(value="SELECT CAST(CAST(SUM(b.durationHour) AS BigDecimal) / CAST(AVG(DATEDIFF(HOUR, c.openTime, c.closeTime) * DATEDIFF(day, :startDate, :endDate)) AS BigDecimal) * 100 AS BigDecimal) " +
            "FROM Booking b " +
            "JOIN Court c " +
            "ON c.ID = b.court.id " +
            " WHERE b.createdAt BETWEEN :startDate AND :endDate " +
            "AND b.status > 0")
    BigDecimal occupancyRateByCreatedAt(LocalDateTime startDate, LocalDateTime endDate);
    @Query(value="SELECT FORMAT(b.createdAt, 'MMMM yyyy') AS month, " +
            "COUNT(b) AS totalCount, AVG(b.durationHour) AS averageHour, " +
            "SUM(b.totalPrice) AS totalPrice, " +
            "CAST(CAST(SUM(b.durationHour) AS BigDecimal) / CAST(AVG(DATEDIFF(HOUR, c.openTime, c.closeTime) * DATEDIFF(day, :startDate, :endDate)) AS BigDecimal) * 100 AS BigDecimal) AS occupancyRate " +
            "FROM Booking b JOIN Court c ON c.id = b.court.id " +
            "WHERE b.createdAt BETWEEN :startDate AND :endDate AND b.status > 0 GROUP BY FORMAT(b.createdAt, 'MMMM yyyy')")
    List<Map<String, Object>> findAllGroupByCreatedAtMonthly(LocalDateTime startDate, LocalDateTime endDate);
    Page<Booking> findAllByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<Booking> findAllByCreatedAtBetweenAndBookingCodeContainsIgnoreCase(LocalDateTime startDate, LocalDateTime endDate, String bookingCode, Pageable pageable);
}