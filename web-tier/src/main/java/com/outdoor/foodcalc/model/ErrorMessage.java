package com.outdoor.foodcalc.model;

/**
 * Response model for server error message.
 *
 * Could be extended later/
 *
 * @author Anton Borovyk.
 */
public class ErrorMessage {

    public String message;

    public ErrorMessage(String message) {
        this.message = message;
    }
}
