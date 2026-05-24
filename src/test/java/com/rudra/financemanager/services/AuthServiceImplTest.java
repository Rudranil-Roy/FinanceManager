package com.rudra.financemanager.services;

import com.rudra.financemanager.dto.auth.AuthResponse;
import com.rudra.financemanager.dto.auth.LoginRequest;
import com.rudra.financemanager.dto.auth.RegisterRequest;
import com.rudra.financemanager.entities.UserEntity;
import com.rudra.financemanager.exceptions.ConflictException;
import com.rudra.financemanager.repositories.UserRepository;
import com.rudra.financemanager.services.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;

    @Mock private HttpServletRequest httpServletRequest;
    @Mock private HttpSession httpSession;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("user@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("John Doe");
        registerRequest.setPhoneNumber("+1234567890");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("user@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_shouldSaveNewUser() {
        when(userRepository.existsByUsername("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        AuthResponse response = authService.register(registerRequest);

        assertEquals("User registered successfully", response.getMessage());
        assertEquals(1L, response.getUserId());

        verify(userRepository).save(any(UserEntity.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void register_shouldThrowConflictWhenDuplicateEmail() {
        when(userRepository.existsByUsername("user@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldCreateSession() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(httpServletRequest.getSession(true)).thenReturn(httpSession);

        AuthResponse response = authService.login(loginRequest, httpServletRequest);

        assertEquals("Login successful", response.getMessage());
        verify(httpSession).setAttribute(anyString(), any());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void logout_shouldInvalidateSession() {
        when(httpServletRequest.getSession(false)).thenReturn(httpSession);

        var response = authService.logout(httpServletRequest);

        assertEquals("Logout successful", response.getMessage());
        verify(httpSession).invalidate();
    }

    @Test
    void logout_shouldWorkEvenWithoutSession() {
        when(httpServletRequest.getSession(false)).thenReturn(null);

        var response = authService.logout(httpServletRequest);

        assertEquals("Logout successful", response.getMessage());
        verify(httpServletRequest).getSession(false);
    }
}
