package com.outdoor.foodcalc;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.model.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Spring advise, that contains all exceptions handlers
 *
 * @author Anton Borovyk.
 */
@ControllerAdvice
public class ExceptionHandlers {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlers.class);

    String getErrorMessage(Exception e) {
        String message = e.getMessage();
        if (message == null) {
            message = e.getClass().getSimpleName();
        }
        return message;
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, ValidationException.class})
    public ResponseEntity<String> validationException(final RuntimeException e) {
        LOG.error("Validation error - {}", e.getMessage());
        return new ResponseEntity<>(getErrorMessage(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> notFoundException(final NotFoundException e) {
        LOG.error("Item was not found -  {}", e.getMessage());
        return new ResponseEntity<>(getErrorMessage(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FoodcalcDomainException.class)
    public ResponseEntity<String> foodcalcDomainException(final FoodcalcDomainException e) {
        LOG.error("Some domain error occurred - {}", e.getMessage());
        return new ResponseEntity<>(getErrorMessage(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> runtimeException(final RuntimeException e) {
        LOG.error("Runtime error", e);
        return new ResponseEntity<>(getErrorMessage(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
