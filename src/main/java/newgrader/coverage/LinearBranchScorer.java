package newgrader.coverage;

/**
 * A linear scorer that considers only branch coverage, not line coverage.
 * For example, if {@link #score(double, double)} is called on a linear branch
 * scorer with max points of 100 and branch coverage of .85, the score 85 would be
 * returned, regardless of the line coverage percent.
 */
public class LinearBranchScorer extends LinearScorer {
    /**
     * Creates a linear scorer that considers only branch coverage, not
     * line coverage.
     *
     * @param maxPoints the maximum number of points
     */
    public LinearBranchScorer(double maxPoints) {
        super(1.0, maxPoints);
    }

    @Override
    protected String getMessage(ClassInfo info) {
        return String.format("Branch coverage is %.0f%%",
                info.branchCoverage());
    }
}
