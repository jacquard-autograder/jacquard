package com.spertus.jacquard.common;

/**
 * The level of support for debugging.
 * <p>
 * As an example, consider reporting of a failure in running Checkstyle.
 * At debug level {@link #NONE}, the {@link Result} would indicate that
 * Checkstyle failed without saying why. At level {@link #LOW}, the
 * {@link Result} might indicate that the Checkstyle jar file could not
 * be found. At level {@link #HIGH}, the program would terminate with a
 * stack trace.
 */
public enum DebugLevel {
    /**
     * No information is shown to users about the cause of problems.
     */
    NONE,

    /**
     * Some information is shown to users about the cause of problems,
     * such as underlying exception messages.
     */
    LOW,

    /**
     * Exceptions are allowed to propagate to the top level to facilitate
     * debugging.
     */
    HIGH
}
