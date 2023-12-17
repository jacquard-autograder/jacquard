package com.spertus.jacquard.common;

import com.google.common.annotations.VisibleForTesting;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The result of an evaluation of student code.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class Result {
    private static final int MAX_MESSAGE_LENGTH = 8192;
    private static final String MESSAGE_OVERFLOW_INDICATOR = "...";

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
        this.message = trimMessage(message, MAX_MESSAGE_LENGTH, MESSAGE_OVERFLOW_INDICATOR);
        this.visibility = visibility;
    }

    @VisibleForTesting
    static String trimMessage(final String message, int maxLength, String overflowIndicator) {
        if (message.length() > maxLength) {
            return message.substring(0, maxLength - overflowIndicator.length())
                    + overflowIndicator;
        } else {
            return message;
        }
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
    public void setVisibility(final Visibility visibility) {
        this.visibility = visibility;
    }

    /**
     * Changes the visibility of this result and returns it.
     *
     * @param visibility the visibility
     * @return this result with the new visibility
     */
    public Result changeVisibility(final Visibility visibility) {
        this.visibility = visibility;
        return this;
    }

    @Override
    public boolean equals(final Object other) {
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
    public static Result makeError(final String name, final Throwable throwable) {
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
    public static Result makeResult(
            final String name,
            final double actualScore,
            final double maxScore,
            final String message) {
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
     * @param message    any message
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
     * @param message  any message
     * @return a result
     */
    public static Result makeFailure(
            final String name,
            final double maxScore,
            final String message) {
        return new Result(name, 0, maxScore, message);
    }

    /**
     * Makes a result with the specified visibility level indicating a total failure.
     *
     * @param name       the name
     * @param maxScore   the number of points not earned
     * @param message    any message
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
     * @param results         the results to summarize
     * @param name            the name of the created result
     * @param allMessage      the message to include if all results are
     *                        successful
     * @param nothingMessage  the message to include if any results are not
     *                        successful
     * @param maxScore        the score if all results are successful
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
        if (results.isEmpty()) {
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

    /**
     * The publication order for results. The default is {@link #NATURAL}.
     * This is used by {@link #reorderResults(List, Order)}.
     */
    public enum Order {
        /**
         * The order in which they were generated. In other words,
         * this will not reorder elements.
         */
        // This relies on Collection.sort() being stable, per API
        // https://stackoverflow.com/a/44452446/631051
        NATURAL((r1, r2) -> 0),

        /**
         * Alphabetical order by result name.
         */
        ALPHABETICAL(Comparator.comparing(r -> r.name)),

        /**
         * In increasing order by maximum score.
         */
        INCREASING_MAX_SCORE(Comparator.comparing(r -> r.maxScore)),

        /**
         * In decreasing order by maximum score.
         */
        DECREASING_MAX_SCORE(Comparator.comparing(r -> -r.maxScore));

        private Comparator<Result> comparator;

        Order(Comparator<Result> comparator) {
            this.comparator = comparator;
        }
    }

    /**
     * Produces a sorted copy of the results.
     *
     * @param results the results
     * @param order   the ordering
     * @return the sorted result list
     */
    public static List<Result> reorderResults(List<Result> results, Order order) {
        // This copies the list rather than sorting in place for two reasons:
        // 1. The list parameter might be immutable.
        // 2. To avoid side effects.
        List<Result> mutableResults = new ArrayList<>(results);
        Collections.sort(mutableResults, order.comparator);
        return mutableResults;
    }
}
