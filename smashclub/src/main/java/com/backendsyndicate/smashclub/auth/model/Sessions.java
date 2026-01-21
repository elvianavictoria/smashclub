package com.backendsyndicate.smashclub.auth.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
//import java.time.LocalDateTime;

@Entity
@Data
public class Sessions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SessionId")
    private String id;

    @Column(name = "SessionToken", length = 500, unique = true, nullable = false)
    private String sessionToken;

    @Column(name = "ExpiresAt", nullable = false)
    private Timestamp expiresAt;

    @Column(name = "CreatedAt", nullable = false)
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "UserId", foreignKey = @ForeignKey(name = "fk_to_user"), nullable = false)
    private User user;

}
