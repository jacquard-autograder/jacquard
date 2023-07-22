package com.spertus.jacquard.exceptions;

/**
 * An exception due to a test timing out.
 * @see com.spertus.jacquard.common.Autograder.Builder#timeout(long) 
 */
public class TimeoutException extends AutograderException {

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeoutException(String message) {
        super(message);
    }
}
