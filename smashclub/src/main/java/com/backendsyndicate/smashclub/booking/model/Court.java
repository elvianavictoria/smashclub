package com.backendsyndicate.smashclub.booking.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class Court {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "CourtCode", length = 50, unique = true, nullable = false)
    private String courtCode;

    @Column(name = "CourtName", length = 100, nullable = false)
    private String courtName;

    @Column(name = "CourtImgLink", length = 500)
    private String courtImgLink;

    @Column(name = "OpenTime", nullable = false)
    private LocalTime openTime;

    @Column(name = "CloseTime", nullable = false)
    private LocalTime closeTime;

    @Column(name = "Status", nullable = false)
    private byte status = 0;

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", insertable = false, nullable = false)
    private LocalDateTime updatedAt;
}
