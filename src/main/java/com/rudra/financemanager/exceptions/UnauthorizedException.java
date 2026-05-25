package com.rudra.financemanager.exceptions;

/**
 * Exception thrown when a user attempts to access a protected endpoint without being authenticated.
 * Resolves to an HTTP 401 Unauthorized status code.
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Constructs a new UnauthorizedException with the specified error message.
     *
     * @param message Detailed error message.
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
