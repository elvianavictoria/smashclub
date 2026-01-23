package com.backendsyndicate.smashclub.payment.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class RefundRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "RefundStatus", nullable = false)
    private byte refundStatus = 0;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

    @Column(name = "UpdatedAt")
    private Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "TransactionID", foreignKey = @ForeignKey(name = "fk_to_trans"), nullable = false)
    private Transaction transaction;
}
