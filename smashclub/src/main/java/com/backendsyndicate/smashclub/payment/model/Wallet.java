package com.backendsyndicate.smashclub.payment.model;

import com.backendsyndicate.smashclub.auth.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
public class Wallet {

    @Column(name = "UserBalance", precision = 17, scale = 2, nullable = false)
    private BigDecimal userBalance = BigDecimal.ZERO;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

    @Column(name = "UpdatedAt")
    private Timestamp updatedAt;

    @Id
    @OneToOne
    @MapsId
    @JoinColumn(name = "UserId")
    private User user;
}

