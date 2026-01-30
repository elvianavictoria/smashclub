package com.backendsyndicate.smashclub.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "UserId")
    @EqualsAndHashCode.Include
    private String id;

    @Column(name = "FullName", length = 150, nullable = false)
    private String fullName;

    @Column(name = "Email", unique = true, nullable = false)
    private String email;

    @Column(name = "PasswordHash", nullable = false)
    private String passwordHash;

    @Column(name = "Status", nullable = false)
    private byte status = 0;

    @Column(name = "FailedLoginAttempt")
    private int failedLoginAttempt = 0;

    @Column(name = "CreatedDate", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "LockedUntil")
    private LocalDateTime lockedUntil;

    @Column(name = "UpdatedDate", insertable = false)
    private LocalDateTime updatedDate;
}