package com.backendsyndicate.smashclub.ecommerce.model;

import com.backendsyndicate.smashclub.auth.model.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Cart {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private int id;

    @Column(name = "Status", nullable = false)
    private int status;

    @OneToOne
    @JoinColumn(name = "UserID", nullable = false)
    private User user;
}
