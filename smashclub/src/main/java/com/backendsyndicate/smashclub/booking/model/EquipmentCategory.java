package com.backendsyndicate.smashclub.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class EquipmentCategory {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "CategoryName", length = 50, nullable = false)
    private String categoryName;

    @Column(name = "Status", nullable = false)
    private int status = 0;
}
