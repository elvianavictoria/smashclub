package com.backendsyndicate.smashclub.ecommerce.model;

import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="Orders")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"user", "transactionId"})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SubTotal", precision = 17, scale = 2, nullable = false)
    private BigDecimal subTotal = BigDecimal.ZERO;

    @Column(name = "TotalPrice", precision = 17, scale = 2,nullable = false)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(name = "Status", nullable = false)
    private byte status = 0;

    @Column(name = "OrderCode")
    private String orderCode = "";

    @Column(name = "OrderDate", nullable = false)
    private LocalDateTime orderDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserId", foreignKey = @ForeignKey(name = "fk_order_to_user"), nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "TransactionID", foreignKey = @ForeignKey(name = "fk_order_to_trx"), nullable = false)
    private Transaction transactionId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItem;
}
