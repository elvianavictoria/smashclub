package com.backendsyndicate.smashclub.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"user"})
public class Sessions {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "session_id")
    @EqualsAndHashCode.Include
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(name = "fk_session_user"),
            nullable = false
    )
    private User user;

    @Column(name = "session_token", length = 500, unique = true, nullable = false)
    private String sessionToken;

    @Column(name = "token_type", nullable = false, length = 20)
    private String tokenType; // "ACCESS", "REFRESH"

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "invalidated_at")
    private LocalDateTime invalidatedAt; // Untuk soft delete/logout

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt; // Update setiap kali dipakai

    @Column(name = "ip_address", length = 45) // Support IPv6 (max 45 chars)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "device_info", length = 100)
    private String deviceInfo;

    // Pre-persist untuk set createdAt
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.lastAccessedAt == null) {
            this.lastAccessedAt = LocalDateTime.now();
        }
    }

    // Helper method untuk cek apakah session masih valid
    public boolean isValid() {
        return this.invalidatedAt == null
                && this.expiresAt.isAfter(LocalDateTime.now());
    }

    // Helper method untuk update last accessed time
    public void updateLastAccessed() {
        this.lastAccessedAt = LocalDateTime.now();
    }
}