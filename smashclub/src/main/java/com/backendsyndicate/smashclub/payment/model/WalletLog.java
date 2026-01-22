package com.backendsyndicate.smashclub.payment.model;

import com.backendsyndicate.smashclub.auth.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
public class WalletLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "LogID")
    private Long id;

    @Column(name = "PreviousBalance", nullable = false, columnDefinition = "DECIMAL(17,2) default 0")
    private BigDecimal previousBalance;

    @Column(name = "CurrentBalance", nullable = false, columnDefinition = "DECIMAL(17,2), default 0")
    private BigDecimal currentBalance;

    @Column(name = "UsageValue", nullable = false, columnDefinition = "DECIMAL(17,2) default 0")
    private BigDecimal usageValue;

    @Column(name = "UsageType", nullable = false)
    private boolean usageType;

    @Column(name = "RefID", nullable = false, columnDefinition = "default  ")
    private String refID;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

}
