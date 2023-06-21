package newgrader.common;

/**
 * The result of a checker.
 *
 * @param name the name of the checker
 * @param score the actual score
 * @param maxScore the maximum possible score
 * @param output an explanation of the result or the empty string
 */
public record Result(String name, double score, double maxScore,
                     String output) {

    public static Result makeTotalFailure(String name, double maxScore, String output) {
        return new Result(name, 0, maxScore, output);
    }

    public static Result makeError(String name, Throwable e) {
        return new Result(name, 0, 0, e.getMessage());
    }

    public static Result makeResult(String name, double actualScore, double maxScore, String output) {
        return new Result(name, actualScore, maxScore, output);
    }

    public static Result makeSuccess(String name, double score, String output) {
        return new Result(name, score, score, output);
    }

    public static Result makeException(String name, double maxScore, String output) {
        return makeResult(name, 0, maxScore, output);
    }
}
