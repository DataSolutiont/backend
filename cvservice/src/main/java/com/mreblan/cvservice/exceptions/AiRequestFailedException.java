package com.mreblan.cvservice.exceptions;

public class AiRequestFailedException extends RuntimeException {
    public AiRequestFailedException(String msg) {
        super(msg);
    }
}
