package com.rudra.financemanager.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Custom error payload returned by the application's global exception handler.
 * Provides standard RFC-7807-like details along with structured field validation errors.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    /**
     * Date and time when the error occurred.
     */
    private LocalDateTime timestamp;

    /**
     * HTTP status code (e.g. 400, 404, 500).
     */
    private int status;

    /**
     * Standard HTTP error phrase (e.g. "Bad Request", "Not Found").
     */
    private String error;

    /**
     * Detailed error message explaining the cause of the failure.
     */
    private String message;

    /**
     * Request URI path that triggered the exception.
     */
    private String path;

    /**
     * Map of specific input field errors (field names to validation messages), if applicable.
     */
    private Map<String, String> fieldErrors;
}
