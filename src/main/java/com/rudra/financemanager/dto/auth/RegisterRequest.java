package com.rudra.financemanager.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user registration requests.
 * Captures user credentials, full name, and phone details, enforcing validation constraints.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    /**
     * The email address used as the login username.
     */
    @NotBlank(message = "Username is required")
    @Email(message = "Username must be a valid email address")
    private String username;

    /**
     * User password. Must be at least 8 characters long.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    /**
     * User's full name.
     */
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    /**
     * User's phone number, verified by pattern regex.
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[+]?[0-9]{7,15}$",
            message = "Phone number must be a valid contact number"
    )
    private String phoneNumber;
}
