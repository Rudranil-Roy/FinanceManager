package com.rudra.financemanager.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rudra.financemanager.config.SecurityConfig;
import com.rudra.financemanager.dto.auth.AuthResponse;
import com.rudra.financemanager.dto.auth.LoginRequest;
import com.rudra.financemanager.dto.auth.RegisterRequest;
import com.rudra.financemanager.dto.common.ApiResponse;
import com.rudra.financemanager.security.CustomUserDetailsService;
import com.rudra.financemanager.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomUserDetailsService  customUserDetailsService;

    @MockitoBean
    private AuthService authService;

    @Test
    void register_shouldReturn201() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user@example.com");
        request.setPassword("password123");
        request.setFullName("John Doe");
        request.setPhoneNumber("+1234567890");

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new AuthResponse("User registered successfully", 1L));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void login_shouldReturn200() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("user@example.com");
        request.setPassword("password123");

        when(authService.login(any(LoginRequest.class), any(HttpServletRequest.class)))
                .thenReturn(new AuthResponse("Login successful", 1L));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    void logout_shouldReturn200() throws Exception {
        when(authService.logout(any(HttpServletRequest.class)))
                .thenReturn(new ApiResponse("Logout successful"));

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }

    @Test
    void register_withInvalidInput_shouldReturn400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("not-an-email");
        request.setPassword("123");
        request.setFullName("");
        request.setPhoneNumber("abc");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}