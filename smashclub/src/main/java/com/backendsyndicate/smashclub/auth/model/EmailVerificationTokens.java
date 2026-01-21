package com.backendsyndicate.smashclub.auth.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
//import java.time.LocalDateTime;

@Entity
@Data
public class EmailVerificationTokens {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="EmailVerificationTokenId")
    private String id;

    @Column(name = "Token", unique = true, nullable = false)
    private String token;

    @Column(name = "ExpiresAt", nullable = false)
    private Timestamp expiresAt;

    @Column(name = "UsedAt")
    private Timestamp usedAt;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "UserId", foreignKey = @ForeignKey(name  = "fk_to_user"), nullable = false)
    private User user;
}
