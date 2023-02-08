package com.outdoor.foodcalc.domain.exception;

/**
 * Exeption occurred on domain service layer
 *
 * @author Olga Borovyk.
 */
public class FoodcalcDomainException extends FoodcalcException {

    public FoodcalcDomainException(String message) {
        super(message);
    }

    public FoodcalcDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
