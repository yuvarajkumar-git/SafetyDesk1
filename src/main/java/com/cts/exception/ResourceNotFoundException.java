package com.cts.exception;

/**
 * Thrown when a requested entity cannot be found.
 * Mapped to HTTP 404 by the global handler.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}