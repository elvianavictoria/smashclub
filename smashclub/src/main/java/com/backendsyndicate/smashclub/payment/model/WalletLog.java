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

    @Column(name = "PreviousBalance", precision = 17, scale = 2, nullable = false)
    private BigDecimal previousBalance =  BigDecimal.ZERO;

    @Column(name = "CurrentBalance", precision = 17, scale = 2, nullable = false)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Column(name = "UsageValue", precision = 17, scale = 2, nullable = false)
    private BigDecimal usageValue = BigDecimal.ZERO;

    @Column(name = "UsageType", nullable = false)
    private boolean usageType;

    @Column(name = "RefID", nullable = false, columnDefinition = "default  ")
    private String refID;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

}
