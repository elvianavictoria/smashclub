package com.backendsyndicate.smashclub.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class EquipmentCategory {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "CategoryName", length = 50, nullable = false)
    private String categoryName;

    @Column(name = "Status", nullable = false)
    private int status;
}
