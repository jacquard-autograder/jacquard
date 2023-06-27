package newgrader.coverage;

import newgrader.common.Result;
import newgrader.exceptions.*;

public class LinearScorer extends Scorer {
    private final double branchWeight;

    /**
     * Creates a scorer that scales the branch percentage and line percentage
     * by the specified weights, which must both be in the range [0, 1] and
     * add up to zero. Weights are provided as integer percentages to reduce
     * floating-point weirdness.
     *
     * @param branchWeight how much to weight the branch coverage percentage
     * @param lineWeight   how much to weight the line coverage percentage
     * @throws ClientException if either weight is not in the range [0, 1] or
     *                         they do not add up to 1
     * @see #score(double, double)
     */
    public LinearScorer(double branchWeight, double lineWeight, double maxPoints) throws ClientException {
        super(maxPoints);
        if (branchWeight < 0 || branchWeight > 1) {
            throw new ClientException(
                    "The branchWeight argument to the LinearScorer constructor must be in the range 0-1 (inclusive).");
        }
        if (lineWeight < 0 || lineWeight > 1) {
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

    // Like the public constructor, but the caller guarantees that the weight
    // is in the range [0-100].
    public LinearScorer(double branchWeight, double maxPoints) {
        super(maxPoints);
        if (branchWeight < 0 || branchWeight > 1) {
            throw new InternalException("Argument to LinearScore(double) is not in range [0-1].");
        }
        this.branchWeight = branchWeight;
    }

    @Override
    public double score(double branchCoverage, double lineCoverage) {
        return maxScore * ((branchCoverage * branchWeight) + (lineCoverage * (1 - branchWeight)));
    }

    @Override
    public Result getResult(ClassInfo info) {
        double points = score(info);
        return new Result(
                "Code coverage",
                points,
                maxScore,
                getMessage(info));
    }

    protected String getMessage(ClassInfo info) {
        return String.format("Branch coverage is %.0f%% and line coverage is %.0f%%",
                100 * info.branchCoverage(), 100 * info.lineCoverage());
    }
}
