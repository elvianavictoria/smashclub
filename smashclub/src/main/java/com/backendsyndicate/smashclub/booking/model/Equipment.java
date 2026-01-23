package com.backendsyndicate.smashclub.booking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EquipmentName", length = 50, nullable = false)
    private String equipmentName;

    @Column(name = "Brand", length = 50)
    private String brand;

    @Column(name = "Type", length = 30)
    private String type;

    @Column(name = "Price", precision = 17, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "Stock", nullable = false)
    private int stock = 0;

    @Column(name = "Description")
    private String description;

    @Column(name = "Status", nullable = false)
    private int status;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "EquipmentCategoryID", nullable = false)
    private EquipmentCategory equipmentCategory;
}
