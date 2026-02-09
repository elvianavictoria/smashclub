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
@ToString(exclude = "equipmentCategory")

public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
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

    @Column(name = "EquipmentImgLink", length = 500)
    private String equipmentImgLink;

    @Column(name = "Description")
    private String description;

    @Column(name = "Status", nullable = false)
    private int status = 0;

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EquipmentCategoryID", foreignKey = @ForeignKey(name = "fk_equip_to_equipCat"), nullable = false)
    private EquipmentCategory equipmentCategory;

    // Need field: EquipmentImgLink
}
