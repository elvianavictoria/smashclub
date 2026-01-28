package com.backendsyndicate.smashclub.booking.model;

import com.backendsyndicate.smashclub.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"user", "court"})

public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "BookingCode", length = 50, nullable = false, unique = true)
    private String bookingCode;

    @Column(name = "BookingDate", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "StartTime", nullable = false)
    private LocalTime startTime;

    @Column(name = "EndTime", nullable = false)
    private LocalTime endTime;

    @Column(name = "DurationHour", nullable = false)
    private int durationHour;

    @Column(name = "BasePrice", precision = 17, scale = 2, nullable = false)
    private BigDecimal basePrice;

    @Column(name = "TotalPrice", precision = 17, scale = 2,nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "Status", nullable = false)
    private byte status = 0;

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", insertable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", foreignKey = @ForeignKey(name = "fk_booking_to_user"), nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CourtID", foreignKey = @ForeignKey(name = "fk_booking_to_court"), nullable = false)
    private Court court;
}
