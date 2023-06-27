package newgrader.common;

/**
 * The result of a checker.
 *
 * @param name     the name of the checker
 * @param score    the actual score
 * @param maxScore the maximum possible score
 * @param output   an explanation of the result or the empty string
 */
public record Result(String name, double score, double maxScore,
                     String output) {

    /**
     * Makes a result indicating a total failure.
     *
     * @param name the name
     * @param maxScore the number of points not earned
     * @param output any output
     * @return a result
     */
    public static Result makeTotalFailure(String name, double maxScore, String output) {
        return new Result(name, 0, maxScore, output);
    }

    /**
     * Makes a result indicating an exceptional event occurred
     *
     * @param name the name
     * @param throwable the underlying {@link Error} or {@link Exception}
     * @return a result
     */
    public static Result makeError(String name, Throwable throwable) {
        return new Result(name, 0, 0, throwable.getMessage());
    }

    /**
     * Makes a result with the provided score.
     *
     * @param name the name
     * @param actualScore the number of points earned
     * @param maxScore the number of points possible
     * @param output any output
     * @return a result
     */
    public static Result makeResult(String name, double actualScore, double maxScore, String output) {
        return new Result(name, actualScore, maxScore, output);
    }

    /**
     * Makes a result indicating a total success.
     *
     * @param name the name
     * @param score the number of points earned
     * @param output any output
     * @return a result
     */
    public static Result makeSuccess(String name, double score, String output) {
        return new Result(name, score, score, output);
    }
}
