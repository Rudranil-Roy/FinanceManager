package com.rudra.financemanager.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for authentication responses.
 * Contains confirmation message and the registered/authenticated user's ID.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {

    /**
     * Response message (e.g. registration success or login success).
     */
    private String message;

    /**
     * The ID of the authenticated user.
     */
    private Long userId;
}
