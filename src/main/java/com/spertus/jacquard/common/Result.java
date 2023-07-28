package com.spertus.jacquard.common;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The result of an evaluation of student code.
 */
public class Result {
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
    public Result(
            final String name,
            final double score,
            final double maxScore,
            final String message,
            final Visibility visibility) {
        this.name = name;
        this.score = score;
        this.maxScore = maxScore;
        this.message = message;
        this.visibility = visibility;
    }

    /**
     * Creates a result with the visibility level specified in
     * {@link Autograder#visibility}.
     *
     * @param name     the name of the checker
     * @param score    the actual score
     * @param maxScore the maximum possible score
     * @param message  an explanation of the result or the empty string
     */
    public Result(
            final String name,
            final double score,
            final double maxScore,
            final String message) {
        this(name, score, maxScore, message, Autograder.getInstance().visibility);
    }

    /**
     * Gets the name of this result.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the score (points earned) of this result.
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

    @Override
    public boolean equals(Object other) {
        if (other instanceof Result result) {
            return name.equals(result.name) &&
                    score == result.score &&
                    maxScore == result.maxScore &&
                    message.equals(result.message);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, score, maxScore, message);
    }

    /**
     * Makes a result indicating an exceptional event occurred
     *
     * @param name      the name
     * @param throwable the underlying {@link Error} or {@link Exception}
     * @return a result
     */
    public static Result makeError(String name, Throwable throwable) {
        return new ExceptionResult(name, throwable);
    }

    /**
     * Makes a result with the provided score.
     *
     * @param name        the name
     * @param actualScore the number of points earned
     * @param maxScore    the number of points possible
     * @param message     any message
     * @return a result
     */
    public static Result makeResult(String name, double actualScore, double maxScore, String message) {
        return new Result(name, actualScore, maxScore, message);
    }

    /**
     * Makes a result indicating a total success.
     *
     * @param name    the name
     * @param score   the number of points earned
     * @param message any message
     * @return a result
     */
    public static Result makeSuccess(
            final String name,
            final double score,
            final String message) {
        return new Result(name, score, score, message);
    }

    /**
     * Makes a result indicating a total success.
     *
     * @param name       the name
     * @param score      the number of points earned
     * @param message     any message
     * @param visibility the visibility level
     * @return a result
     */
    public static Result makeSuccess(
            final String name,
            final double score,
            final String message,
            final Visibility visibility) {
        return new Result(name, score, score, message, visibility);
    }


    /**
     * Makes a result indicating a total failure.
     *
     * @param name     the name
     * @param maxScore the number of points not earned
     * @param message   any message
     * @return a result
     */
    public static Result makeFailure(String name, double maxScore, String message) {
        return new Result(name, 0, maxScore, message);
    }

    /**
     * Makes a result with the specified visibility level indicating a total failure.
     *
     * @param name       the name
     * @param maxScore   the number of points not earned
     * @param message     any message
     * @param visibility the visibility level
     * @return a result
     */
    public static Result makeFailure(
            final String name,
            final double maxScore,
            final String message,
            final Visibility visibility) {
        return new Result(name, 0, maxScore, message, visibility);
    }

    /**
     * Creates a single result summarizing a list of results, giving credit only
     * if all the results indicate complete success (having a score equal to
     * the maximum score}. Otherwise, the returned result has a score of 0.
     * <p>
     * The message of the new result begins with either {@code
     * allMessage} (for all successful) or {@code nothingMessage}. If
     * {@code includeMessages} is true, the messages of the individual results
     * will be appended to the message of the produced result.
     * <p>
     * All of the passed results must have the same visibility level, which
     * will be used for the created result.
     *
     * @param results        the results to summarize
     * @param name           the name of the created result
     * @param allMessage     the message to include if all results are
     *                       successful
     * @param nothingMessage the message to include if any results are not
     *                       successful
     * @param maxScore       the score if all results are successful
     * @param includeMessages whether to include the messages of the results
     * @return a new result
     * @throws IllegalArgumentException if all the passed results do not have
     *                                  the same visibility level or if there
     *                                  are no passed results
     */
    public static Result makeAllOrNothing(
            final List<Result> results,
            final String name,
            final String allMessage,
            final String nothingMessage,
            final double maxScore,
            final boolean includeMessages) {

        // Verify validity of arguments and set visibility.
        if (results.size() == 0) {
            throw new IllegalArgumentException("Results argument is empty");
        }
        final Visibility visibility = results.get(0).getVisibility();
        if (results.stream().anyMatch((Result r) -> r.getVisibility() != visibility)) {
            throw new IllegalArgumentException(
                    "All results passed to makeAllOrNothing() must have the same visibility level");
        }

        // Build the message.
        final String messages = includeMessages ? results.
                stream().
                map(Result::getMessage).
                collect(Collectors.joining("\n"))
                : "";

        // Make the final success or failure message.
        if (results.
                stream().
                allMatch((Result r) -> r.getScore() == r.getMaxScore())) {
            return makeSuccess(name, maxScore, allMessage + "\n" + messages, visibility);
        }
        return makeFailure(name, maxScore, nothingMessage + "\n" + messages, visibility);
    }

    /**
     * Changes the visibility of all the results.
     *
     * @param results    the results
     * @param visibility the new visibility
     */
    public static void changeVisibility(
            final List<Result> results, final Visibility visibility) {
        results.forEach((Result r) -> r.setVisibility(visibility));
    }
}
