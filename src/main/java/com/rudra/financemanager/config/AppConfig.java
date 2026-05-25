package com.rudra.financemanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;

/**
 * General application configuration class.
 * Defines shared Spring beans such as PasswordEncoder and Clock.
 */
@Configuration
public class AppConfig {

    /**
     * Configures the password encoder bean using BCrypt hashing.
     * Used across the application for secure password hashing and verification.
     *
     * @return The BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the system default timezone clock.
     * Useful for dependency-injecting time to allow consistent testing.
     *
     * @return The standard system Clock.
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
