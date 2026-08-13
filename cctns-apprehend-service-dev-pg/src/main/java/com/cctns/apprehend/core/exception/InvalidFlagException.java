package com.cctns.apprehend.core.exception;

public class InvalidFlagException extends RuntimeException {
    public InvalidFlagException(String message) {
        super(message);
    }

    public InvalidFlagException(String message,Exception ex) {
        super(message,ex);
    }
}
