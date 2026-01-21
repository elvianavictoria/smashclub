package com.backendsyndicate.smashclub.booking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
public class Coach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CoachCode", length = 50, unique = true, nullable = false)
    private String coachCode;

    @Column(name = "CoachName", length = 100, nullable = false)
    private String coachName;

    @Column(name = "PricePerHour", nullable = false, columnDefinition = "DECIMAL(17,2)")
    private BigDecimal pricePerHour;

    @Column(name = "Status", nullable = false)
    private int status;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

    @Column(name = "UpdatedAt", nullable = false)
    private Timestamp updatedAt;

}
