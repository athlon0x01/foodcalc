package com.outdoor.foodcalc.domain.exception;

public class FoodcalcException extends RuntimeException {

    public FoodcalcException(String message) {
        super(message);
    }

    public FoodcalcException(String message, Throwable cause) {
        super(message, cause);
    }
}
