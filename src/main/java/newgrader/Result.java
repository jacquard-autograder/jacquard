package newgrader;

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

    static Result makeFailure(String name, double maxScore, String message) {
        return new Result(name, 0, maxScore, message);
    }
}
