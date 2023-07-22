package com.spertus.jacquard.coverage;

import com.spertus.jacquard.exceptions.*;
import com.spertus.jacquard.common.Result;

/**
 * A code coverage scorer that uses a linear equation to weight the branch
 * coverage and line coverage.
 */
public class LinearScorer extends Scorer {
    private final double branchWeight;

    /**
     * Creates a scorer that scales the branch percentage and line percentage
     * by the specified weights, which must both be in the range [0, 1] and
     * add up to one.
     *
     * @param branchWeight how much to weight the branch coverage percentage
     * @param lineWeight   how much to weight the line coverage percentage
     * @param maxPoints    the maximum number of points
     * @throws ClientException if either weight is not in the range [0, 1] or
     *                         they do not add up to 1
     */
    public LinearScorer(double branchWeight, double lineWeight, double maxPoints) throws ClientException {
        super(maxPoints);
        if (branchWeight < 0.0 || branchWeight > 1.0) {
            throw new ClientException(
                    "The branchWeight argument to the LinearScorer constructor must be in the range 0-1 (inclusive).");
        }
        if (lineWeight < 0.0 || lineWeight > 1.0) {
            throw new ClientException(
                    "The lineWeight argument to the LinearScorer constructor must be in the range 0-1 (inclusive).");
        }
        if (branchWeight + lineWeight != 1.0) {
            // Do I have to worry about rounding errors?
            throw new ClientException(
                    "When calling LinearScore(double, double), the sum of the two arguments must be 1."
            );
        }

        this.branchWeight = branchWeight;
    }

    /**
     * Creates a scorer that scales the branch percentage by the specified
     * weight, which must be in the range [0, 1]. The remainder of the score
     * is determined by scaling the line percentage by (1-branchWeight).
     *
     * @param branchWeight how much to weight the branch coverage percentage
     * @param maxPoints    the maximum number of points
     * @throws ClientException if either weight is not in the range [0, 1]
     */
    public LinearScorer(double branchWeight, double maxPoints) {
        super(maxPoints);
        if (branchWeight < 0 || branchWeight > 1) {
            throw new InternalException("Argument to LinearScore(double) is not in range [0-1].");
        }
        this.branchWeight = branchWeight;
    }

    @Override
    public double score(double branchCoverage, double lineCoverage) {
        return maxScore * (branchCoverage * branchWeight + lineCoverage * (1 - branchWeight));
    }

    @Override
    public Result getResult(double branchCoverage, double lineCoverage) {
        double points = score(branchCoverage, lineCoverage);
        return new Result(
                "Code coverage",
                points,
                maxScore,
                getMessage(branchCoverage, lineCoverage));
    }

    /**
     * Gets a message summarizing information about the coverage being tested.
     *
     * @param branchCoverage the branch coverage ratio [0, 1]
     * @param lineCoverage   the line coverage ratio [0, 1]
     * @return a message
     */
    protected String getMessage(double branchCoverage, double lineCoverage) {
        return String.format("Branch coverage is %.0f%% and line coverage is %.0f%%",
                100 * branchCoverage, 100 * lineCoverage);
    }
}
