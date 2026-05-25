package com.rudra.financemanager.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard REST API response wrapper.
 * Holds success/informative messages returned to the client.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    /**
     * Informational response message (e.g. success messages, operation notifications).
     */
    private String message;
}
