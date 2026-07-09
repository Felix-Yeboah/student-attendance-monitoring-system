package com.ucc.attendance.exception;

/**
 * Indicates that user input does not satisfy the application rules.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
