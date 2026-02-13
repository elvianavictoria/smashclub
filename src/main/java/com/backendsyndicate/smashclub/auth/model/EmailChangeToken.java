package com.backendsyndicate.smashclub.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"user"})
public class EmailChangeToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "EmailChangeTokenId")
    @EqualsAndHashCode.Include
    private String id;

    @Column(name = "Token", unique = true, nullable = false)
    private String token;

    @Column(name = "OldEmail", nullable = false, length = 100)
    private String oldEmail;

    @Column(name = "NewEmail", nullable = false, length = 100)
    private String newEmail;

    @Column(name = "ExpiresAt", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "UsedAt")
    private LocalDateTime usedAt;

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "UserId",
            foreignKey = @ForeignKey(name = "fk_email_change_to_user"),
            nullable = false
    )
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}