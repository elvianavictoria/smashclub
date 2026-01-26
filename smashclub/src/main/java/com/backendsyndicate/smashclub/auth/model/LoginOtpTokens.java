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

public class LoginOtpTokens {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "LoginOtpTokenId")
    @EqualsAndHashCode.Include
    private String id;

    @Column(name = "OtpCode", length = 10, nullable = false)
    private String otpCode;

    @Column(name = "ExpiresAt", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "UsedAt")
    private LocalDateTime usedAt;

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserId", foreignKey = @ForeignKey(name = "fk_to_user"), nullable = false)
    private User user;
}
