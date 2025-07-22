package com.ecommerce.auth_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    private String role;

    public UserDto() {
    }

    public UserDto(Long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
