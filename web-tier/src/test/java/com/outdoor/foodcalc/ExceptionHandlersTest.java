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
    public void illegalArgumentExceptionTest() {
        final String negative_value = "negative value";
        ResponseEntity<ErrorMessage> errorResponse =
            handlers.illegalArgumentException(new IllegalArgumentException(negative_value));
        assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
        assertNotNull(errorResponse.getBody());
        assertEquals(negative_value, errorResponse.getBody().message);
    }

    @Test
    public void illegalArgumentExceptionNoMessageTest() {
        ResponseEntity<ErrorMessage> errorResponse = handlers.illegalArgumentException(new IllegalArgumentException());
        assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
        assertNotNull(errorResponse.getBody());
        assertEquals("IllegalArgumentException", errorResponse.getBody().message);
    }
}