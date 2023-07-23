package com.spertus.jacquard.exceptions;

/**
 * A checked exception due to a test timing out.
 * @see com.spertus.jacquard.common.Autograder.Builder#timeout(long)
 */
public class TimeoutException extends Exception implements AutograderException {

    /**
     * Creates a new TimeoutException.
     *
     * @param message the message
     * @param cause the cause
     */
    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new TimeoutException.
     *
     * @param message the message
     */
    public TimeoutException(String message) {
        super(message);
    }
}
