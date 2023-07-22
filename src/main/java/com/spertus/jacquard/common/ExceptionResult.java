package com.spertus.jacquard.common;

/**
 * A result encapsulating an exception.
 */
public class ExceptionResult extends Result {
    /**
     * Makes a result indicating an exceptional event occurred.
     *
     * @param name      the name
     * @param throwable the underlying {@link Error} or {@link Exception}
     */
    public ExceptionResult(String name, Throwable throwable) {
        super(name, 0, 0, throwable.getMessage());
    }
}
