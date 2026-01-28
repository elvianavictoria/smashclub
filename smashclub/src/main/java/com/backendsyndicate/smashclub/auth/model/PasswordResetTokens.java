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

public class PasswordResetTokens {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name ="PasswordResetTokenId")
    @EqualsAndHashCode.Include
    private String id;

    @Column(name = "Token", nullable = false, unique = true)
    private String token;

    @Column(name = "ExpiresAt", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "UsedAt")
    private LocalDateTime usedAt;

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserId", foreignKey = @ForeignKey(name = "fk_password_reset_to_user"), nullable = false)
    private User user;
}
