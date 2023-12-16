package com.spertus.jacquard.publisher;

import com.spertus.jacquard.common.Result;

import java.util.List;

/**
 * This class contains code to publish the results of grading to an external
 * grading system, such as Gradescope.
 *
 * @see com.spertus.jacquard.common.Visibility
 */
public abstract class Publisher {
    /**
     * Serializes the results in the format appropriate for the external grading
     * tool.
     *
     * @param results the results
     * @return a string representation of the results
     */
    public abstract String serializeResults(List<Result> results);

    /**
     * Publishes the results in a manner appropriate for the external grading
     * tool. This does nothing (and returns {@code false}) if it detects that
     * it is not running within the tool (i.e., is on a development machine).
     *
     * @param results the results
     * @return whether the results were successfully published
     */
    public boolean publishResults(List<Result> results) {
        return publishResults(results, Result.Order.NATURAL);
    }

    /**
     * Publishes the results in a manner appropriate for the external grading
     * tool. This does nothing (and returns {@code false}) if it detects that
     * it is not running within the tool (i.e., is on a development machine).
     *
     * @param results the results
     * @param order how to order the results
     * @return whether the results were successfully published
     */
    public abstract boolean publishResults(List<Result> results, Result.Order order);

    /**
     * Displays the results in a human-readable format.
     *
     * @param results the results
     */
    public abstract void displayResults(List<Result> results);
}
