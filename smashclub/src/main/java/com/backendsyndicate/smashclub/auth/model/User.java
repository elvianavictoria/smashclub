package com.backendsyndicate.smashclub.auth.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
//import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "UserId")
    private String id;

    @Column(name = "FullName", length = 150, nullable = false)
    private String fullName;

    @Column(name = "Email", unique = true, nullable = false)
    private String email;

    @Column(name = "PasswordHash", nullable = false)
    private String passwordHash;

    @Column(name = "Status", nullable = false)
    private byte status;

    @Column(name = "FailedLoginAttempt")
    private int failedLoginAttempt = 0;

    @Column(name = "CreatedDate", updatable = false, nullable = false)
    private Timestamp createdDate;

    @Column(name = "LockedUntil")
    private Timestamp lockedUntil;

    @Column(name = "UpdatedDate")
    private Timestamp updatedDate;

//    @OneToMany(mappedBy = "user")
//    private List<EmailVerificationTokens> emailVerifyTokens;
//
//    @OneToMany(mappedBy = "user")
//    private List<Sessions> sessions;
//
//    @OneToMany(mappedBy = "user")
//    private List<PasswordResetTokens> passwordResetTokens ;
//
//    @OneToOne(mappedBy = "user")
//    @PrimaryKeyJoinColumn
//    private Wallet wallet;
}