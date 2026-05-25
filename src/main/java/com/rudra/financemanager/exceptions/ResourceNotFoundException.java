package com.rudra.financemanager.exceptions;

/**
 * Exception thrown when a requested resource (user, transaction, category, goal) cannot be found.
 * Resolves to an HTTP 404 Not Found status code.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified error message.
     *
     * @param message Detailed error message.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
