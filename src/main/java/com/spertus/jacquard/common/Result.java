package com.spertus.jacquard.common;

import java.util.List;
import java.util.stream.Collectors;

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
     * @param name     the name
     * @param maxScore the number of points not earned
     * @param output   any output
     * @return a result
     */
    public static Result makeTotalFailure(String name, double maxScore, String output) {
        return new Result(name, 0, maxScore, output);
    }

    /**
     * Makes a result indicating an exceptional event occurred
     *
     * @param name      the name
     * @param throwable the underlying {@link Error} or {@link Exception}
     * @return a result
     */
    public static Result makeError(String name, Throwable throwable) {
        return new Result(name, 0, 0, throwable.getMessage());
    }

    /**
     * Makes a result with the provided score.
     *
     * @param name        the name
     * @param actualScore the number of points earned
     * @param maxScore    the number of points possible
     * @param output      any output
     * @return a result
     */
    public static Result makeResult(String name, double actualScore, double maxScore, String output) {
        return new Result(name, actualScore, maxScore, output);
    }

    /**
     * Makes a result indicating a total success.
     *
     * @param name   the name
     * @param score  the number of points earned
     * @param output any output
     * @return a result
     */
    public static Result makeSuccess(String name, double score, String output) {
        return new Result(name, score, score, output);
    }

    /**
     * Creates a single result summarizing a list of results, giving credit only
     * if all the results indicate complete success (having {@link #score()}
     * equal to {@link #maxScore()}. Otherwise, the returned result has a score
     * of 0.
     * <p>
     * The {@link #output()} of the new result begins with either {@code
     * allMessage} (for all successful) or {@code nothingMessage}. If
     * {@code includeOutputs} is true, the outputs of the individual results
     * will be appended to the output of the produced result.
     *
     * @param results        the results to summarize
     * @param name           the name of the created result
     * @param allMessage     the message to include if all results are
     *                       successful
     * @param nothingMessage the message to include if any results are not
     *                       successful
     * @param maxScore       the score if all results are successful
     * @param includeOutputs whether to include the outputs of the results
     * @return a new result
     */
    public static Result makeAllOrNothing(
            List<Result> results,
            String name,
            String allMessage,
            String nothingMessage,
            double maxScore,
            boolean includeOutputs) {
        String outputs = includeOutputs ? results.
                stream().
                map(Result::output).
                collect(Collectors.joining("\n"))
                : "";
        if (results.
                stream().
                allMatch((Result r) -> r.score() == r.maxScore())) {
            return makeSuccess(name, maxScore, allMessage + "\n" + outputs);
        }
        return makeTotalFailure(name, maxScore, nothingMessage + "\n" + outputs);
    }
}
