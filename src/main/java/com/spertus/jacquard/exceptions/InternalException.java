package com.spertus.jacquard.exceptions;

/**
 * Signals that an internal error occurred within the autograder.
 */
public class InternalException extends Exception
        implements AutograderException {
    /**
     * Constructs an {@code InternalException} with the specified
     * message.
     *
     * @param message an explanation
     */
    public InternalException(final String message) {
        super(message);
    }

    /**
     * Constructs an {@code InternalException} with the specified
     * message and cause.
     *
     * @param message an explanation
     * @param cause   the underlying cause
     */
    public InternalException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an {@code InternalException} with the specified cause.
     *
     * @param cause the underlying cause
     */
    public InternalException(final Throwable cause) {
        super("An internal autograder error occurred.", cause);
    }
}
