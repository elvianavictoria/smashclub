package com.backendsyndicate.smashclub.booking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Racket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "RacketBrand", length = 100, nullable = false)
    private String brand;

    @Column(name = "RacketType", length = 50, nullable = false)
    private String type;

    @Column(name = "Description")
    private String description;

    @Column(name = "Price", nullable = false, columnDefinition = "DECIMAL(17,2)")
    private BigDecimal price;

    @Column(name = "Stock", nullable = false, columnDefinition = "default 0")
    private int stock;

    @Column(name = "Status", nullable = false)
    private int status;

    @OneToOne
    @JoinColumn(name = "EquipmentID", nullable = false)
    private Equipment equipment;
}
