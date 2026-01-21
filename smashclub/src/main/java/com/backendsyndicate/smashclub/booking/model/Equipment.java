package com.backendsyndicate.smashclub.booking.model;

import jakarta.persistence.*;
import lombok.Data;

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

//    @Column(name = "EquipmentCategory", nullable = false)
//    private enum ganti?

//    @Column(name = "Type", nullable = false)
//    private String type; ganti?

    @Column(name = "Status", nullable = false)
    private int status;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;
}
