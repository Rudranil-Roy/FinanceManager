package com.rudra.financemanager.exceptions;

/**
 * Exception thrown when a request has invalid arguments, formats, or is otherwise malformed.
 * Resolves to an HTTP 400 Bad Request status code.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Constructs a new BadRequestException with the specified error message.
     *
     * @param message Detailed error message.
     */
    public BadRequestException(String message) {
        super(message);
    }
}
