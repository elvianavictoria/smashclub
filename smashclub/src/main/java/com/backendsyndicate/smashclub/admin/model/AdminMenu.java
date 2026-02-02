package com.backendsyndicate.smashclub.admin.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "adminRoles")

public class AdminMenu {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
//    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "MenuCode")
    private String menuCode;

    @Column(name = "MenuName", nullable = false)
    private String menuName;

    @Column(name = "MenuRoute", nullable = false)
    private String menuRoute;

    @Column(name = "ParentID", nullable = false)
    private int parentId; //fk?

    @Column(name = "Status", nullable = false)
    private int status = 0;

    @ManyToMany(mappedBy = "menuSet")
    Set<AdminRole>  adminRoles;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CategoryID", foreignKey = @ForeignKey(name = "fk_menuCategory_to_menu"), nullable = false)
    AdminMenuCategory category;
}
