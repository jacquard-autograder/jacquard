package newgrader.exceptions;

/**
 * Signals that an error occurred within the autograder due to misconfiguration.
 */
public class ClientException extends Exception {
    /**
     * Constructs an {@code ClientException} with the specified
     * message and cause.
     *
     * @param message an explanation
     * @param cause   the underlying cause
     */
    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an {@code ClientException} with the specified
     * message and cause.
     *
     * @param message an explanation
     */
    public ClientException(String message) {
        super(message);
    }
}