package com.spertus.jacquard.exceptions;

/**
 * A checked exception indicating a problem with a submission.
 */
public class SubmissionException extends Exception
        implements AutograderException {
    /**
     * Constructs a new submission exception with the given message.
     *
     * @param message the message
     */
    public SubmissionException(final String message) {
        super(message);
    }
}
