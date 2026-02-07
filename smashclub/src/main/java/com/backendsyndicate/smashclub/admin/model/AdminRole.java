package com.backendsyndicate.smashclub.admin.model;

import jakarta.persistence.*;
import lombok.*;


import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"menuSet", "permissionSet"})

public class AdminRole {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private int id;

    @Column(name = "RoleCode", length = 50, nullable = false)
    private String roleCode;

    @Column(name = "RoleName", length = 50, nullable = false)
    private String roleName;

    @Column(name = "Status", nullable = false)
    private int status = 0;

    @ManyToMany
    @JoinTable(name = "AdminRoleMenu", joinColumns = @JoinColumn(name = "RoleID"), inverseJoinColumns = @JoinColumn(name = "MenuID"))
    private Set<AdminMenu> menuSet;

    @ManyToMany
    @JoinTable(name = "AdminRolePermission", joinColumns = @JoinColumn(name = "RoleID"), inverseJoinColumns = @JoinColumn(name = "PermissionID"))
    private Set<AdminPermission> permissionSet;
}
