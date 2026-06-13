package com.cts.exception;

/**
 * Thrown when an operation conflicts with existing state, e.g. a work permit
 * overlapping another active permit at the same location. Mapped to HTTP 409.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}