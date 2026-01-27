package com.backendsyndicate.smashclub.payment.model;

import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.ecommerce.model.Order;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name="Transactions")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"user", "orders"})

public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "TransactionCode", length = 20,unique = true, nullable = false)
    private String transactionCode;

    @Column(name = "TransactionLabel", nullable = false)
    private String transactionLabel;

    @Column(name = "TotalPrice", precision = 17, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "PaymentLink", length = 2048)
    private String paymentLink = " ";

    @Column(name = "Status", nullable = false)
    private byte status = 0;

    @Column(name = "IsRefunded", nullable = false)
    private byte isRefunded = 0;

    @Column(name = "ReferenceCode", nullable = false)
    private String referenceCode = " ";

    @Column(name = "Notes")
    private String notes;

    @Column(name = "TransactionType")
    private int transactionType;

    @Column(name = "PaymentMethodID")
    private int paymentMethodID;

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private Timestamp createdAt;

    @Column(name = "UpdatedAt", insertable = false)
    private Timestamp updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserId", foreignKey = @ForeignKey(name = "fk_to_user"), nullable = false)
    private User user;

    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY)
    private List<Order> orders;
}
