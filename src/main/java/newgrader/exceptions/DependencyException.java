package newgrader.exceptions;

/**
 * Signals that an application that this depends on failed.
 */
public class DependencyException extends AutograderException {
    public DependencyException(String s, Throwable cause) {
        super(s, cause);
    }
}
