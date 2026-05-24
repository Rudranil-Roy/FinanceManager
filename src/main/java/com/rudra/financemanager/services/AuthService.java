package com.rudra.financemanager.services;

import com.rudra.financemanager.dto.auth.AuthResponse;
import com.rudra.financemanager.dto.auth.LoginRequest;
import com.rudra.financemanager.dto.auth.RegisterRequest;
import com.rudra.financemanager.dto.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest, HttpServletRequest httpServletRequest);
    ApiResponse logout(HttpServletRequest httpServletRequest);
}
