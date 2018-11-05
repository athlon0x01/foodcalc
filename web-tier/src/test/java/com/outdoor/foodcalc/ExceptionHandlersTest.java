package com.outdoor.foodcalc;

import com.outdoor.foodcalc.model.ErrorMessage;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

/**
 * JUnit tests for {@link ExceptionHandlers}.
 *
 * @author Anton Borovyk.
 */
public class ExceptionHandlersTest {

    private ExceptionHandlers handlers = new ExceptionHandlers();

    @Test
    public void getErrorMessageTest() {
        final String negative_value = "negative value";
        assertEquals(negative_value, handlers.getErrorMessage(new IllegalArgumentException(negative_value)));
    }

    @Test
    public void getErrorMessageNoMessageTest() {
        assertEquals("IllegalArgumentException", handlers.getErrorMessage(new IllegalArgumentException()));
    }

    @Test
    public void validationExceptionTest() {
        final String negative_value = "negative value";
        ResponseEntity<ErrorMessage> errorResponse =
            handlers.validationException(new IllegalArgumentException(negative_value));
        assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
        assertNotNull(errorResponse.getBody());
        assertEquals(negative_value, errorResponse.getBody().message);
    }

    @Test
    public void validationExceptionNoMessageTest() {
        ResponseEntity<ErrorMessage> errorResponse = handlers.runtimeException(new RuntimeException());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getStatusCode());
        assertNotNull(errorResponse.getBody());
        assertEquals("RuntimeException", errorResponse.getBody().message);
    }
}