package com.backendsyndicate.smashclub.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "ProductName", nullable = false)
    private String productName;

    @Column(name = "ProductDesc", length = 2000)
    private String productDesc;

    @Column(name = "Category", length = 100, nullable = false)
    private String category;

    @Column(name = "Status", nullable = false)
    private byte status = 0;

    @Column(name = "DefaultImgLink", length = 500, nullable = false)
    private String defaultImgLink;

    @OneToMany(mappedBy = "product")
    private List<ProductVariant> productVariants;
}
