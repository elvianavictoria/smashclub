package com.backendsyndicate.smashclub.booking.model;

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
@ToString(exclude = {"coach", "booking"})

public class CoachDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "CoachHour", nullable = false)
    private int coachHour;

    @Column(name = "BookingDate", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "StartTime", nullable = false)
    private LocalTime startTime;

    @Column(name = "EndTime", nullable = false)
    private LocalTime endTime;

    @Column(name = "CoachPrice", precision = 17, scale = 2, nullable = false)
    private BigDecimal coachPrice;

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch =  FetchType.LAZY, optional = false)
    @JoinColumn(name = "CoachID", foreignKey = @ForeignKey(name = "fk_to_coach"), nullable = false)
    private Coach coach;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BookingID", foreignKey = @ForeignKey(name = "fk_to_booking"), nullable = false)
    private Booking booking;
}
