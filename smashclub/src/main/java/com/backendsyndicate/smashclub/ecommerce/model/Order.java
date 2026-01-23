package com.backendsyndicate.smashclub.ecommerce.model;

import com.backendsyndicate.smashclub.auth.model.User;
import com.backendsyndicate.smashclub.payment.model.Transaction;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TotalAmount", precision = 17, scale = 2,nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "Status", nullable = false)
    private byte status;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "UserId", nullable = false)
    private User userId;

    @OneToMany
    @JoinColumn(name = "TransactionID", nullable = false)
    private List<Transaction> transactions;
}
