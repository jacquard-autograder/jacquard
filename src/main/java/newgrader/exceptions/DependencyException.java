package newgrader.exceptions;

/**
 * Signals that an application that this depends on failed.
 */
public class DependencyException extends AutograderException {
    public DependencyException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DependencyException(String msg) {
        super(msg);
    }
}
