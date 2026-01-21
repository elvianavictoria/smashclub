package com.backendsyndicate.smashclub.ecommerce.model;

import com.backendsyndicate.smashclub.auth.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TotalAmount", nullable = false, columnDefinition = "DECIMAL(17,2)")
    private BigDecimal totalAmount;

    @Column(name = "Status", nullable = false)
    private int status;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "UserId", nullable = false)
    private User userId;

//    TransactionID
}
