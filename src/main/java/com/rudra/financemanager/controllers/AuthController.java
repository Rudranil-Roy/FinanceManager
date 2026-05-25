package com.rudra.financemanager.controllers;

import com.rudra.financemanager.dto.auth.AuthResponse;
import com.rudra.financemanager.dto.auth.LoginRequest;
import com.rudra.financemanager.dto.auth.RegisterRequest;
import com.rudra.financemanager.dto.common.ApiResponse;
import com.rudra.financemanager.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling user authentication endpoints.
 * Provides endpoints for registration, login, and logout.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user.
     *
     * @param request DTO containing the registration details (username, password, full name, phone).
     * @return ResponseEntity with registration details and HTTP 201 status.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates a user and starts a session.
     * Sets a session cookie JSESSIONID.
     *
     * @param request DTO containing username and password.
     * @param httpServletRequest the servlet request to save security context.
     * @return ResponseEntity containing login success message.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletRequest httpServletRequest) {
        AuthResponse response = authService.login(request, httpServletRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Logs out the currently authenticated user by invalidating the HTTP session.
     *
     * @param httpServletRequest the current servlet request.
     * @return ResponseEntity with logout confirmation.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest httpServletRequest) {
        ApiResponse response = authService.logout(httpServletRequest);
        return ResponseEntity.ok(response);
    }
}
