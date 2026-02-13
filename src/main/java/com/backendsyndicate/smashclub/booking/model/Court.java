package com.backendsyndicate.smashclub.booking.model;

import com.backendsyndicate.smashclub.common.constant.BookingConstant;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    @Column(name = "PricePerHour", precision = 17, scale = 2,nullable = false)
    private BigDecimal pricePerHour = BigDecimal.ZERO;

    @Column(name = "Status", nullable = false)
    private byte status = BookingConstant.RESOURCE_ACTIVE;

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();  // ✅ SET VALUE SAAT INSERT!
        if (status == 0) {
            status = BookingConstant.RESOURCE_ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}