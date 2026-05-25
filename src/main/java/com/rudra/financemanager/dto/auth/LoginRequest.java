package com.rudra.financemanager.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object containing login credentials.
 * Includes user username (email) and password.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    /**
     * User's email address used as their username.
     */
    @NotBlank(message = "Username is required")
    @Email(message = "Username must be a valid email address")
    private String username;

    /**
     * User's clear-text password.
     */
    @NotBlank(message = "Password is required")
    private String password;
}
