package com.backendsyndicate.smashclub.payment.model;

import com.backendsyndicate.smashclub.auth.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name="Transactions")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TransactionCode", length = 20,unique = true, nullable = false)
    private String transactionCode;

    @Column(name = "TransactionLabel", nullable = false)
    private String transactionLabel;

    @Column(name = "TotalPrice", nullable = false, columnDefinition = "DECIMAL(17,2)")
    private BigDecimal totalPrice;

    @Column(name = "PaymentLink", length = 2048, columnDefinition = "default ")
    private String paymentLink;

    @Column(name = "Status", nullable = false, columnDefinition = "default 0")
    private byte status;

    @Column(name = "IsRefunded", nullable = false, columnDefinition = "default 0")
    private byte isRefunded;

    @Column(name = "ReferenceCode", nullable = false, columnDefinition = "default ''")
    private String referenceCode;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

    @Column(name = "UpdatedAt   ")
    private Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "UserId", foreignKey = @ForeignKey(name = "fk_to_user"), nullable = false)
    private User user;
}
