package com.rudra.financemanager.services;

import com.rudra.financemanager.dto.auth.AuthResponse;
import com.rudra.financemanager.dto.auth.LoginRequest;
import com.rudra.financemanager.dto.auth.RegisterRequest;
import com.rudra.financemanager.dto.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Service interface for handling user registration, authentication, and session cleanup.
 */
public interface AuthService {

    /**
     * Registers a new user.
     * Enforces username uniqueness and hashes passwords before storage.
     *
     * @param registerRequest DTO containing username, password, full name, and phone.
     * @return AuthResponse containing details of the registered user.
     */
    AuthResponse register(RegisterRequest registerRequest);

    /**
     * Authenticates credentials and initiates a session.
     * Integrates with Spring Security's AuthenticationManager to construct the security context.
     *
     * @param loginRequest       DTO containing user login credentials.
     * @param httpServletRequest HttpServletRequest used to set/save the new security context.
     * @return AuthResponse containing the login outcome.
     */
    AuthResponse login(LoginRequest loginRequest, HttpServletRequest httpServletRequest);

    /**
     * Terminate the active authentication session.
     * Clears the Spring Security context and invalidates the session cookie.
     *
     * @param httpServletRequest HttpServletRequest containing the user's active session.
     * @return ApiResponse confirming successful logout.
     */
    ApiResponse logout(HttpServletRequest httpServletRequest);
}
