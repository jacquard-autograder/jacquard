package newgrader.common;

/**
 * The result of a checker.
 *
 * @param name the name of the checker
 * @param score the actual score
 * @param maxScore the maximum possible score
 * @param message an explanation of the result or the empty string
 */
public record Result(String name, double score, double maxScore,
                     String message) {

    public static Result makeTotalFailure(String name, double maxScore, String message) {
        return new Result(name, 0, maxScore, message);
    }

    public static Result makeError(String name, Throwable e) {
        return new Result(name, 0, 0, e.getMessage());
    }

    public static Result makeResult(String name, double actualScore, double maxScore, String message) {
        return new Result(name, actualScore, maxScore, message);
    }

    public static Result makeSuccess(String name, double score, String message) {
        return new Result(name, score, score, message);
    }

    public static Result makeException(String name, double maxScore, String message) {
        return makeResult(name, 0, maxScore, message);
    }
}
