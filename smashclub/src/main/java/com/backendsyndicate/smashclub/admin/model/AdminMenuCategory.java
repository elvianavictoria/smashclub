package com.backendsyndicate.smashclub.admin.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "adminMenu")

public class AdminMenuCategory {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private int id;

    @Column(name = "CategoryName", nullable = false)
    private String categoryName;

    @Column(name = "Status", nullable = false)
    private int status = 0;

    @OneToMany(mappedBy = "category")
    List<AdminMenu> adminMenu;
}
