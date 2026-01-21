package com.backendsyndicate.smashclub.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "Quantity", nullable = false)
    private int quantity;

    @Column(name = "Price", nullable = false, columnDefinition = "DECIMAL(17,2)")
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "OrderID", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "VariantID", nullable = false)
    private ProductVariant variant;


}
