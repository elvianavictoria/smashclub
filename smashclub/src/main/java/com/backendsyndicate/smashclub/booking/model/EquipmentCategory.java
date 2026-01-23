package com.backendsyndicate.smashclub.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class EquipmentCategory {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "BallType", length = 50, nullable = false)
    private String ballType;

    @Column(name = "BallBrand", length = 20)
    private String ballBrand;

    @Column(name = "RacketBrand", length = 100, nullable = false)
    private String racketBrand;

    @Column(name = "RacketType", length = 50, nullable = false)
    private String racketType;

    @Column(name = "Price", precision = 17, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "Stock", nullable = false)
    private int stock = 0;

    @Column(name = "Status", nullable = false)
    private int status;
}
