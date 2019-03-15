package com.outdoor.foodcalc.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Any validation exception
 *
 * @author Anton Borovyk.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Validation error")
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
