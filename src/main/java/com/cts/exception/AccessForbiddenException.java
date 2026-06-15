package com.cts.exception;

/**
 * Thrown when a request is authenticated-but-forbidden, or when login is
 * refused due to account state (Inactive/Transferred/Locked). Maps to HTTP 403.
 */
public class AccessForbiddenException extends RuntimeException {

    public AccessForbiddenException(String message) {
        super(message);
    }
}