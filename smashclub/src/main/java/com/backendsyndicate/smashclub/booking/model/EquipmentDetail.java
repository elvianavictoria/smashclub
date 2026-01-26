package com.backendsyndicate.smashclub.booking.model;

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
@ToString(exclude = {"booking", "equipment"})

public class EquipmentDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "Quantity", nullable = false)
    private int quantity;

    @Column(name = "StartTime", nullable = false)
    private LocalTime startTime;

    @Column(name = "EndTime", nullable = false)
    private LocalTime endTime;

    @Column(name = "EquipmentPrice", precision = 17, scale = 2, nullable = false)
    private BigDecimal equipmentPrice;

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "BookingID", foreignKey = @ForeignKey(name = "fk_to_booking"), nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "Equipment", foreignKey = @ForeignKey(name = "fk_to_equip"), nullable = false)
    private Equipment equipment;
}
