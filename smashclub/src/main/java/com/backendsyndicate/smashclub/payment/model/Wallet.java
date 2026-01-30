package com.backendsyndicate.smashclub.payment.model;

import com.backendsyndicate.smashclub.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"user", "walletLogs"})
public class Wallet {
    @Id
    @EqualsAndHashCode.Include
    @Column(name = "ID")
    private String userId;

    @Column(name = "UserBalance", precision = 17, scale = 2, nullable = false)
    private BigDecimal userBalance = BigDecimal.ZERO;

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", insertable = false)
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "UserId")
    private User user;

    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY)
    private List<WalletLog> walletLogs;
}

