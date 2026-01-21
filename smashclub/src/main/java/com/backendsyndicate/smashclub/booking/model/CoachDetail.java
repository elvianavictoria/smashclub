package com.backendsyndicate.smashclub.booking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Data
public class CoachDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CoachHour", nullable = false)
    private int coachHour;

    @Column(name = "BookingDate", nullable = false)
    private Date bookingDate;

    @Column(name = "StartTime", nullable = false)
    private Time startTime;

    @Column(name = "EndTime", nullable = false)
    private Time endTime;

    @Column(name = "CoachPrice", nullable = false, columnDefinition = "DECIMAL(17,2)")
    private BigDecimal coachPrice;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "CoachID", nullable = false)
    private Coach coach;

//    @OneToOne?? BookingID
}
