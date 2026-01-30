package com.backendsyndicate.smashclub.admin.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "menu")

public class AdminPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private int id;

    @Column(name = "PermissionName", nullable = false)
    private String permissionName;

    @Column(name = "Status", nullable = false)
    private int status = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MenuID", foreignKey = @ForeignKey(name = "fk_permission_to_menu"), nullable = false)
    private AdminMenu menu;

    @ManyToMany(mappedBy = "permissionSet")
    private Set<AdminRole> adminRoles;
}
