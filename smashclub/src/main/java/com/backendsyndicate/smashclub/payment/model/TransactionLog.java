package com.backendsyndicate.smashclub.payment.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class TransactionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private String id;

    @Column(name = "PreviousStatus", nullable = false)
    private int previousStatus;

    @Column(name = "CurrentStatus", nullable = false)
    private int currentStatus;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

    /* TransactionLog to Transaction? */
}
