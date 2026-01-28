package com.backendsyndicate.smashclub.payment.model;

import com.backendsyndicate.smashclub.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class WalletLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LogID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "PreviousBalance", precision = 17, scale = 2, nullable = false)
    private BigDecimal previousBalance =  BigDecimal.ZERO;

    @Column(name = "CurrentBalance", precision = 17, scale = 2, nullable = false)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Column(name = "UsageValue", precision = 17, scale = 2, nullable = false)
    private BigDecimal usageValue = BigDecimal.ZERO;

    @Column(name = "UsageType", nullable = false)
    private boolean usageType;

    @Column(name = "RefID", nullable = false)
    private String refID = " ";

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private Timestamp createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserId", nullable = false)
    private Wallet wallet;
}
