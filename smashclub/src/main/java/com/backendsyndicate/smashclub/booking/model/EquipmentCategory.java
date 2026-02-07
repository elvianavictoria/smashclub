package com.backendsyndicate.smashclub.booking.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class EquipmentCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "CategoryName", length = 50, nullable = false)
    private String categoryName;

    @Column(name = "Status", nullable = false)
    private int status = 0;
}
