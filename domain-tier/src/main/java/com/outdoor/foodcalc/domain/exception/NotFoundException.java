package com.outdoor.foodcalc.domain.exception;

/**
 * If expected entity was not found exception
 *
 * @author Anton Borovyk.
 */
public class NotFoundException extends FoodcalcException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
