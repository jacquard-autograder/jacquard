package com.spertus.jacquard.common;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The result of an evaluation of student code.
 */
public class Result {
    /**
     * The default visibility of results.
     */
    public static final Visibility DEFAULT_VISIBILITY = Visibility.VISIBLE;

    private final String name;
    private final double score;
    private final double maxScore;
    private final String message;
    private Visibility visibility;

    /**
     * Creates a result with the specified properties.
     *
     * @param name       the name of the checker
     * @param score      the actual score
     * @param maxScore   the maximum possible score
     * @param message    an explanation of the result or the empty string
     * @param visibility the visibility of the result to the student
     */
    public Result(String name, double score, double maxScore,
                  String message, Visibility visibility) {
        this.name = name;
        this.score = score;
        this.maxScore = maxScore;
        this.message = message;
        this.visibility = visibility;
    }

    /**
     * Creates a result with the default visibility.
     *
     * @param name     the name of the checker
     * @param score    the actual score
     * @param maxScore the maximum possible score
     * @param message  an explanation of the result or the empty string
     * @see #DEFAULT_VISIBILITY
     */
    public Result(String name, double score, double maxScore,
                  String message) {
        this.name = name;
        this.score = score;
        this.maxScore = maxScore;
        this.message = message;
        this.visibility = DEFAULT_VISIBILITY;
    }

    /**
     * Gets the name of the result
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the score (points earned) of the result.
     *
     * @return the score
     */
    public double getScore() {
        return score;
    }

    /**
     * Gets the maximum possible score of the test associated with this result.
     *
     * @return the maximum score
     */
    public double getMaxScore() {
        return maxScore;
    }

    /**
     * Gets the message with any additional information about this result.
     *
     * @return the message, possibly the empty string
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the visibility level of this result.
     *
     * @return the visibility
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * Sets the visibility of this result.
     *
     * @param visibility the visibility
     */
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    /**
     * Changes the visibility of this result and returns it.
     *
     * @param visibility the visibility
     * @return this result with the new visibility
     */
    public Result changeVisibility(Visibility visibility) {
        this.visibility = visibility;
        return this;
    }

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
        return new Result(name, score, score, output, DEFAULT_VISIBILITY);
    }

    /**
     * Creates a single result summarizing a list of results, giving credit only
     * if all the results indicate complete success (having a score equal to
     * the maximum score}. Otherwise, the returned result has a score of 0.
     * <p>
     * The message of the new result begins with either {@code
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
                map(Result::getMessage).
                collect(Collectors.joining("\n"))
                : "";
        if (results.
                stream().
                allMatch((Result r) -> r.getScore() == r.getMaxScore())) {
            return makeSuccess(name, maxScore, allMessage + "\n" + outputs);
        }
        return makeTotalFailure(name, maxScore, nothingMessage + "\n" + outputs);
    }

    /**
     * Changes the visibility of all the results.
     *
     * @param results    the results
     * @param visibility the new visibility
     */
    public static void changeVisibility(List<Result> results, Visibility visibility) {
        results.forEach((Result r) -> r.setVisibility(visibility));
    }
}
