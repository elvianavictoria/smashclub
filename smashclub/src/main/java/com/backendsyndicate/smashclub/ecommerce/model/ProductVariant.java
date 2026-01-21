package com.backendsyndicate.smashclub.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;


import java.math.BigDecimal;

@Entity
@Data
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Name", length = 100, nullable = false)
    private String name;

    @Column(name = "VariantImgLink", length = 500)
    private String variantImgLink;

    @Column(name = "Sku", length = 100, nullable = false, unique = true)
    private String sku;

    @Column(name = "Price", nullable = false, columnDefinition = "DECIMAL(17,2)")
    private BigDecimal price;

    @Column(name = "Stock", nullable = false, columnDefinition = "default 0")
    private int stock;

    @ManyToOne
    @JoinColumn(name = "ProductID", nullable = false)
    private Product product;
}
