package com.ecommerce.exception;

/**
 * BadRequestException — thrown for invalid operations.
 * e.g., trying to cancel an already-shipped order → 400 response.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
