package com.spertus.jacquard.exceptions;

public class TimeoutException extends AutograderException {

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeoutException(String message) {
        super(message);
    }
}
