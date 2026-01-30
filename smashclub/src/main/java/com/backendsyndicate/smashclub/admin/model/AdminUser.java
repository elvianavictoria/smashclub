package com.backendsyndicate.smashclub.admin.model;

import com.backendsyndicate.smashclub.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"adminRole", "user"})

public class AdminUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "Username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "Full Name", length = 50, nullable = false)
    private String fullName;

    @Column(name = "Status", nullable = false)
    private int status = 0;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", insertable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RoleID", foreignKey = @ForeignKey(name = "fk_user_to_role"), nullable = false)
    private AdminRole adminRole;

    @OneToOne
    @JoinColumn(name = "UserID", foreignKey = @ForeignKey(name = "fk_adminUser_to_user"), nullable = false )
    private User user;
}
