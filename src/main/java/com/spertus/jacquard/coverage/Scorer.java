package com.spertus.jacquard.coverage;

import com.spertus.jacquard.common.Result;

/**
 * A way of converting branch coverage and line coverage information
 * into a single score.
 */
public abstract class Scorer {
    /**
     * The maximum score that can be earned.
     */
    protected double maxScore;

    /**
     * Creates a scorer.
     *
     * @param maxScore the maximum possible score
     */
    protected Scorer(double maxScore) {
        this.maxScore = maxScore;
    }

    /**
     * Converts branch coverage and line coverage percentages into a score.
     *
     * @param branchCoverage the percentage of branches covered [0, 1]
     * @param lineCoverage   the percentage of lines covered [0, 1]
     * @return a nonnegative score
     */
    public abstract double score(double branchCoverage, double lineCoverage);

    /**
     * Computes the score based on information generated by Jacoco.
     *
     * @param info information generated by Jacoco
     * @return the score
     */
    protected double score(ClassInfo info) {
        return score(info.branchCoverage(), info.lineCoverage());
    }

    /**
     * Creates a result based on information generated by Jacoco.
     *
     * @param info information generated by Jacoco
     * @return the result
     */
    public abstract Result getResult(ClassInfo info);
}