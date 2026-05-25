package com.rudra.financemanager.services.impl;

import com.rudra.financemanager.dto.auth.AuthResponse;
import com.rudra.financemanager.dto.auth.LoginRequest;
import com.rudra.financemanager.dto.auth.RegisterRequest;
import com.rudra.financemanager.dto.common.ApiResponse;
import com.rudra.financemanager.entities.UserEntity;
import com.rudra.financemanager.exceptions.ConflictException;
import com.rudra.financemanager.repositories.UserRepository;
import com.rudra.financemanager.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link AuthService}.
 * Orchestrates business logic for user account registration, credentials checking, and sessions.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    /**
     * Registers a new user account.
     * Validates that the username (email) is not already registered.
     * Password is encoded using the configured {@link PasswordEncoder}.
     *
     * @param registerRequest DTO containing register parameters.
     * @return AuthResponse containing details of the registered user.
     * @throws ConflictException if a user with the same email already exists.
     */
    @Override
    @Transactional
    public AuthResponse register(final RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ConflictException("User already exists with this email");
        }

        final UserEntity userEntity = UserEntity.builder()
                .username(registerRequest.getUsername().toLowerCase().trim())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName().trim())
                .phoneNumber(registerRequest.getPhoneNumber() != null ? registerRequest.getPhoneNumber().trim() : null)
                .build();

        final UserEntity savedUser = userRepository.save(userEntity);

        return AuthResponse.builder()
                .message("User registered successfully")
                .userId(savedUser.getId())
                .build();
    }

    /**
     * Authenticates a user's credentials against the AuthenticationManager and stores the
     * resulting security context into the active session repository.
     *
     * @param loginRequest       DTO containing user credentials.
     * @param request The current HttpServletRequest context.
     * @return AuthResponse indicating login outcome.
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(final LoginRequest loginRequest, final HttpServletRequest request) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername().toLowerCase().trim(),
                        loginRequest.getPassword()
                )
        );

        final SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);

        securityContextRepository.saveContext(context, request, null);

        return AuthResponse.builder()
                .message("Login successful")
                .build();
    }

    /**
     * Terminates the user's HTTP session and wipes the Spring Security context.
     *
     * @param request The active HTTP request.
     * @return ApiResponse detailing logout outcome.
     */
    @Override
    public ApiResponse logout(final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        securityContextHolderStrategy.clearContext();

        return new ApiResponse("Logout successful");
    }
}