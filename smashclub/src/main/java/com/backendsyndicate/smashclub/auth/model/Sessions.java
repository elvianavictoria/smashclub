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

public class Sessions {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "SessionId")
    @EqualsAndHashCode.Include
    private String id;

    @Column(name = "SessionToken", length = 500, unique = true, nullable = false)
    private String sessionToken;

    @Column(name = "ExpiresAt", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserId", foreignKey = @ForeignKey(name = "fk_session_to_user"), nullable = false)
    private User user;
}
