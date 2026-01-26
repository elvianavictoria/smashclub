package com.backendsyndicate.smashclub.booking.model;

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

    @Column(name = "PricePerHour", precision = 17, scale = 2,nullable = false)
    private BigDecimal pricePerHour;

    @Column(name = "Status", nullable = false)
    private byte status = 0;

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", insertable = false)
    private LocalDateTime updatedAt;
}
