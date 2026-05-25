package com.rudra.financemanager.exceptions;

/**
 * Exception thrown when an authenticated user attempts to access a resource they do not own or have permission for.
 * Resolves to an HTTP 403 Forbidden status code.
 */
public class ForbiddenException extends RuntimeException {

    /**
     * Constructs a new ForbiddenException with the specified error message.
     *
     * @param message Detailed error message.
     */
    public ForbiddenException(String message) {
        super(message);
    }
}
