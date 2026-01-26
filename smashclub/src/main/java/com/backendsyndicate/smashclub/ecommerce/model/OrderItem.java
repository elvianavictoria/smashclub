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

    @Column(name = "Quantity", nullable = false)
    private int quantity;

    @Column(name = "Price", precision = 17, scale = 2,nullable = false)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OrderID", foreignKey = @ForeignKey(name = "fk_to_order"), nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "VariantID", foreignKey = @ForeignKey(name = "fk_to_var"), nullable = false)
    private ProductVariant variant;
}
