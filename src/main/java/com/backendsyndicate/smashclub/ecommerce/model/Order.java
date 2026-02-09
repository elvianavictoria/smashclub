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
@ToString(exclude = {"user", "transaction"})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TotalAmount", precision = 17, scale = 2,nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "Status", nullable = false)
    private byte status = 0;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserId", foreignKey = @ForeignKey(name = "fk_order_to_user"), nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "TransactionID", foreignKey = @ForeignKey(name = "fk_order_to_trans"), nullable = false)
    private Transaction transaction;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItem;
}
