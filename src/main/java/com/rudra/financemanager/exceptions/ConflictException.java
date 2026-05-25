package com.rudra.financemanager.exceptions;

/**
 * Exception thrown when a resource already exists or there is a state conflict (e.g. duplicate username).
 * Resolves to an HTTP 409 Conflict status code.
 */
public class ConflictException extends RuntimeException {

    /**
     * Constructs a new ConflictException with the specified error message.
     *
     * @param message Detailed error message.
     */
    public ConflictException(String message) {
        super(message);
    }
}
