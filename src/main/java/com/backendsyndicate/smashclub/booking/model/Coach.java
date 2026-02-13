package com.backendsyndicate.smashclub.booking.model;

import com.backendsyndicate.smashclub.common.constant.BookingConstant;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class Coach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "CoachCode", length = 50, unique = true, nullable = false)
    private String coachCode;

    @Column(name = "CoachName", length = 100, nullable = false)
    private String coachName;

    @Column(name = "CoachImgLink", length = 500)
    private String coachImgLink;

    @Column(name = "PricePerHour", precision = 17, scale = 2,nullable = false)
    private BigDecimal pricePerHour;

    @Column(name = "Status", nullable = false)
    private byte status = BookingConstant.RESOURCE_ACTIVE;

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt", insertable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Need field: CoachImgLink
}
