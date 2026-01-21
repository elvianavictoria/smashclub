package com.backendsyndicate.smashclub.payment.model;

import com.backendsyndicate.smashclub.auth.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
public class Wallet {
    @Id
    @Column(name = "UserID")
    private String userId;

    @Column(name = "UserBalance", nullable = false, columnDefinition = "DECIMAL(17,2) default 0")
    private BigDecimal userBalance;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

    @Column(name = "UpdatedAt")
    private Timestamp updatedAt;

    @OneToOne
    @MapsId
    @JoinColumn(name = "UserID")
    private User user;
}
