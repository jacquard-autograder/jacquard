package newgrader.coverage;

/**
 * A way of converting branch coverage and line coverage information
 * into a single score.
 */
@FunctionalInterface
public interface Scorer {
    /**
     * Converts branch coverage and line coverage percentages into a score.
     *
     * @param branchCoverage the percentage of branches covered [0, 1]
     * @param lineCoverage the percentage of lines covered [0, 1]
     * @param maxPoints the
     * @return a nonnegative score
     */
    double score(double branchCoverage, double lineCoverage, double maxPoints);
}
