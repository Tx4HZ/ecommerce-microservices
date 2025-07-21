package com.ecommerce.auth_service.entity;

import jakarta.persistence.*;
import lombok.Data;

//@Entity
@Data
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String role;
}
