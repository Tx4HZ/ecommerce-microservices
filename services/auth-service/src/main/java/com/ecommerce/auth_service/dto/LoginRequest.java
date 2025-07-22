package com.ecommerce.auth_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequest {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("role")
    private String role;

    public LoginRequest(Long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }
}
