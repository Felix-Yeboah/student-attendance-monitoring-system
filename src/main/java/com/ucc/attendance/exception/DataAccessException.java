package com.ucc.attendance.exception;

/**
 * Converts low-level SQL problems into an application-specific unchecked exception.
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
