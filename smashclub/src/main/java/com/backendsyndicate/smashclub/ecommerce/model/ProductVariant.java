package com.backendsyndicate.smashclub.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "product")

public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "Name", length = 100, nullable = false)
    private String name;

    @Column(name = "VariantImgLink", length = 500)
    private String variantImgLink;

    @Column(name = "Sku", length = 100, nullable = false, unique = true)
    private String sku;

    @Column(name = "Price", precision = 17, scale = 2,nullable = false)
    private BigDecimal price;

    @Column(name = "Stock", nullable = false)
    private int stock = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ProductID", foreignKey = @ForeignKey(name = "fk_to_product"), nullable = false)
    private Product product;
}
