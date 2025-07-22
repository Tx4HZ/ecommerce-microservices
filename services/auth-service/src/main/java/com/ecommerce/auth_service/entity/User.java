package com.ecommerce.auth_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role = "ROLE_USER";
}
