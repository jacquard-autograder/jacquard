package newgrader.coverage;

/**
 * A linear scorer that considers only line coverage, not branch coverage.
 * For example, if {@link #score(double, double, double)} is called with
 * line coverage of .95 and maximum points of 10, the score 9.5 would be
 * returned, regardless of the branch coverage percent.
 */
public class LinearLineScorer extends LinearScorer {
    /**
     * Creates a linear scorer that considers only line coverage, not
     * branch coverage.
     */
    public LinearLineScorer() {
       super(0);
    }
}
