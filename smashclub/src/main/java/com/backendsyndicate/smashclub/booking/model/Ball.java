package com.backendsyndicate.smashclub.booking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Ball {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "BallType", length = 50, nullable = false)
    private String ballType;

    @Column(name = "BallBrand", length = 20, nullable = false)
    private String ballBrand;

    @Column(name = "Description")
    private String description;

    @Column(name = "Price", nullable = false, columnDefinition = "DECIMAL(17,2)")
    private BigDecimal price;

    @Column(name = "Stock", nullable = false)
    private int stock;

    @Column(name = "Status", nullable = false)
    private int status;

    @OneToOne
    @JoinColumn(name = "EquipmentID", nullable = false)
    private Equipment equipment;
}
