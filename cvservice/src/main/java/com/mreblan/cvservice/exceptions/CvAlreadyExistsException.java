package com.mreblan.cvservice.exceptions;

public class CvAlreadyExistsException extends RuntimeException {
    public CvAlreadyExistsException(String msg) {
        super(msg);
    }
}
