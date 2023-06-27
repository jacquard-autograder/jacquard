package com.spertus.jacquard.exceptions;

/**
 * Signals an exceptional circumstance within the autograder library.
 */
public abstract class AutograderException extends RuntimeException {
    /**
     * Constructs an {@code AutograderException} with the specified
     * message and cause.
     *
     * @param message an explanation
     * @param cause   the underlying cause
     */
    public AutograderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an {@code AutograderException} with the specified
     * message and cause.
     *
     * @param message an explanation
     */
    public AutograderException(String message) {
        super(message);
    }
}
