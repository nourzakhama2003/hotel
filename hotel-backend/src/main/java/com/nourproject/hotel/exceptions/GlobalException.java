package com.nourproject.hotel.exceptions;

/**
 * Custom unchecked exception for business logic errors
 */
public class GlobalException extends RuntimeException {

    public GlobalException(String message) {
        super(message);
    }

    public GlobalException(String message, Throwable cause) {
        super(message, cause);
    }
}
