package com.backendsyndicate.smashclub.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"cart", "variant"})

public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "Quantity", nullable = false)
    private int quantity;

    @Column(name = "PriceSnapshot", precision = 17, scale = 2, nullable = false)
    private BigDecimal priceSnapshot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CartID", foreignKey = @ForeignKey(name = "fk_cartItem_to_cart"), nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Variant", foreignKey = @ForeignKey(name = "fk_cartItem_to_var"), nullable = false)
    private ProductVariant variant;
}
