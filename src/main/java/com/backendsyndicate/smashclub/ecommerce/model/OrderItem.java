package com.backendsyndicate.smashclub.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"order", "variant"})

public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "ProductName", unique = true)
    private String productName;

    @Column(name = "Category")
    private String category;

    @Column(name = "Quantity", nullable = false)
    private int quantity = 0;

    @Column(name = "Price", precision = 17, scale = 2,nullable = false)
    private BigDecimal priceAtPurchase = BigDecimal.ZERO;

    @Column(name = "OrderItemImgLink")
    private String orderItemImgLink;

    @Column(name = "TotalPrice", precision = 17, scale = 2,nullable = false)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OrderID", foreignKey = @ForeignKey(name = "fk_orderItem_to_order"), nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "VariantID", foreignKey = @ForeignKey(name = "fk_orderItem_to_var"), nullable = false)
    private ProductVariant variant;
}
