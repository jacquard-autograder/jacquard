package com.spertus.jacquard.exceptions;

import com.spertus.jacquard.common.Autograder;

/**
 * Signals that an error occurred within the autograder due to misuse of the
 * API, such as calling {@link Autograder#init()} repeatedly or passing an
 * invalid argument.
 */
public class ClientException extends RuntimeException
        implements AutograderException {
    /**
     * Constructs a {@code ClientException} with the specified
     * message and cause.
     *
     * @param message an explanation
     * @param cause   the underlying cause
     */
    public ClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a {@code ClientException} with the specified
     * message and cause.
     *
     * @param message an explanation
     */
    public ClientException(final String message) {
        super(message);
    }
}
