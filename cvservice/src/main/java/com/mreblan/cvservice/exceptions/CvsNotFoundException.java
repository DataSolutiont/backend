package com.mreblan.cvservice.exceptions;

public class CvsNotFoundException extends RuntimeException {
    public CvsNotFoundException(String msg) {
        super(msg);
    }
}
