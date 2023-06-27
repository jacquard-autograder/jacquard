package newgrader.exceptions;

/**
 * Signals that an application that this depends on failed.
 */
public class DependencyException extends AutograderException {
    /**
     * Creates an exception indicating child application failure.
     *
     * @param message a message
     * @param cause the underlying cause
     */
    public DependencyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an exception indicating child application failure.
     *
     * @param message a message
     */
    public DependencyException(String message) {
        super(message);
    }
}
