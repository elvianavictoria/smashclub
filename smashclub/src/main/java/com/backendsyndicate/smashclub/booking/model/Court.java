package com.backendsyndicate.smashclub.booking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Data
public class Court {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CourtCode", length = 50, unique = true, nullable = false)
    private String courtCode;

    @Column(name = "CourtName", length = 100, nullable = false)
    private String courtName;

    @Column(name = "OpenTime", nullable = false)
    private Time openTime;

    @Column(name = "CloseTime", nullable = false)
    private Time closeTime;

    @Column(name = "Status", nullable = false)
    private byte status;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

    @Column(name = "UpdatedAt", nullable = false)
    private Timestamp updatedAt;

}
