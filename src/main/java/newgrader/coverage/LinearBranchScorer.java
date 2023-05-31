package newgrader.coverage;

/**
 * A linear scorer that considers only branch coverage, not line coverage.
 * For example, if {@link #score(double, double, double)} is called with
 * branch coverage of .85 and maximum points of 100, the score 85 would be
 * returned, regardless of the line coverage percent.
 */
public class LinearBranchScorer extends LinearScorer {
    /**
     * Creates a linear scorer that considers only branch coverage, not
     * line coverage.
     */
    public LinearBranchScorer() {
        super(1.0);
    }
}
