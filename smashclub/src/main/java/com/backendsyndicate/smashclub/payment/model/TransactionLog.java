package com.backendsyndicate.smashclub.payment.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
public class TransactionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PreviousStatus", nullable = false)
    private byte previousStatus;

    @Column(name = "CurrentStatus", nullable = false)
    private byte currentStatus;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

    @OneToMany
    @JoinColumn(name = "TransactionID", nullable = false)
    private List<Transaction> transactions;
}
