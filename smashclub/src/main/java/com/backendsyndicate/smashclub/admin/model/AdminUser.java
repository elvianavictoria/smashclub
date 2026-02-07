package com.backendsyndicate.smashclub.admin.model;

import com.backendsyndicate.smashclub.auth.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"adminRole", "user"})

public class AdminUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "Username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "Full Name", length = 50, nullable = false)
    private String fullName;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Status", nullable = false)
    private int status = 0;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt", insertable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RoleID", foreignKey = @ForeignKey(name = "fk_user_to_role"), nullable = false)
    private AdminRole adminRole;

    /** disini letak role dari user nya yang akan di baca di API nanti */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<AdminPermission> lt = this.adminRole.getPermissionSet();
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (AdminPermission m :lt) {
            grantedAuthorities.add(new SimpleGrantedAuthority(m.getPermissionCode()));
        }
        return grantedAuthorities;
    }
}
