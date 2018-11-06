package com.outdoor.foodcalc.model;

/**
 * Any validation exception
 *
 * @author Anton Borovyk.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
