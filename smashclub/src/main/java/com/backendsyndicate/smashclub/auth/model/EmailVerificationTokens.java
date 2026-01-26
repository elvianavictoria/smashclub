package com.backendsyndicate.smashclub.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"user"})

public class EmailVerificationTokens {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="EmailVerificationTokenId")
    @EqualsAndHashCode.Include
    private String id;

    @Column(name = "Token", unique = true, nullable = false)
    private String token;

    @Column(name = "ExpiresAt",nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "UsedAt")
    private LocalDateTime usedAt;

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserId", foreignKey = @ForeignKey(name  = "fk_to_user"), nullable = false)
    private User user;
}

