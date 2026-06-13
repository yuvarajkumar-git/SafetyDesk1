package com.cts.exception;

/**
 * Thrown when creating a resource violates a uniqueness rule
 * (e.g. duplicate Email). Mapped to HTTP 409 by the global handler.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}