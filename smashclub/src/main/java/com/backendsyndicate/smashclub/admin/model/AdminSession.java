package com.backendsyndicate.smashclub.admin.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "adminUser")

public class AdminSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "LoginTime", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "ExpiredTime", nullable = false)
    private LocalDateTime expiredTime;

    @Column(name = "LoginToken", nullable = false)
    private String loginToken;

    @Column(name = "Status", nullable = false)
    private int status = 0;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", insertable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", foreignKey = @ForeignKey(name = "fk_to_user"), nullable = false)
    private AdminUser adminUser;
}
