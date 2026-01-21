package com.backendsyndicate.smashclub.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "ProductName", nullable = false)
    private String productName;

    @Column(name = "ProductDesc", length = 2000)
    private String productDesc;

    @Column(name = "Category", length = 100, nullable = false)
    private String category;

    @Column(name = "Status", nullable = false)
    private byte status;

    @Column(name = "DefaultImgLink", length = 500, nullable = false)
    private String defaultImgLink;
}
