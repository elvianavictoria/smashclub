package com.backendsyndicate.smashclub.booking.model;

import com.backendsyndicate.smashclub.auth.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BookingCode", length = 50, nullable = false, unique = true)
    private String bookingCode;

    @Column(name = "BookingDate", nullable = false)
    private Timestamp bookingDate;

    @Column(name = "StartTime", nullable = false)
    private Time startTime;

    @Column(name = "EndTime", nullable = false)
    private Time endTime;

    @Column(name = "DurationHour", nullable = false)
    private int durationHour;

    @Column(name = "BasePrice", precision = 17, scale = 2, nullable = false)
    private BigDecimal basePrice;

    @Column(name = "TotalPrice", precision = 17, scale = 2,nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "Status", nullable = false)
    private byte status;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

    @Column(name = "UpdatedAt", nullable = false)
    private Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;

    @OneToMany
    @JoinColumn(name = "CourtID", nullable = false)
    private List<Court> courts;
}
