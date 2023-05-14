package newgrader;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import java.util.List;

/**
 * The base class for counters to test whether the number of occurrences of an
 * element is within the specified range.
 */
public abstract class Counter implements Processor {
    private final String counterName;
    private final String countedName;
    private final double maxScore;
    private final int minCount;
    private final int maxCount;

    /**
     * Creates a new counter to test whether the number of occurrences of an
     * element is within the specified range.
     *
     * @param counterName the name of this processor (for the {@link Result})
     * @param countedName the name of the element (for the {@link Result})
     * @param maxScore    the score if the condition holds
     * @param minCount    the minimum number of occurrences
     * @param maxCount    the maximum number of occurrences, or {@link Integer#MAX_VALUE}
     *                    if there is no limit.
     * @throws IllegalArgumentException if minCount < 0 or maxCount < minCount,
     *                                  or if minCount is 0 when maxCount is {@link Integer#MAX_VALUE}
     */
    public Counter(String counterName, String countedName, double maxScore, int minCount, int maxCount) {
        if (minCount < 0) {
            throw new IllegalArgumentException("minCount must be >= 0");
        }
        if (maxCount < minCount) {
            throw new IllegalArgumentException("maxCount must be >= minCount");
        }
        if (minCount == 0 && maxCount == Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "There is no reason to create a Counter of 0 or more elements");
        }

        this.counterName = counterName;
        this.countedName = countedName;
        this.maxScore = maxScore;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    // The adapter is not set in the constructor because it might not be
    // available until this constructor completes.
    protected abstract VoidVisitorAdapter<MutableInteger> getAdapter();

    @Override
    public List<Result> process(CompilationUnit cu) {
        MutableInteger mi = new MutableInteger();
        getAdapter().visit(cu, mi);
        return List.of(getResult(mi));
    }

    @Override
    public double getTotalMaxScore() {
        return this.maxScore;
    }

    private String getPrefix() {
        if (maxCount == Integer.MAX_VALUE) {
            return String.format("Code was required to have at least %d %s", minCount, countedName);
        }

        if (minCount == 0) {
            return String.format("Code was required to have up to %d %s", maxCount, countedName);
        }
        return String.format("Code was required to have %d-%d %s", minCount, maxCount, countedName);
    }

    public Result getResult(MutableInteger mi) {
        if (mi.getValue() < minCount) {
            return new Result(counterName, 0, maxScore,
                    String.format("%s but had only %d", getPrefix(), mi.getValue()));
        } else if (mi.getValue() > maxCount) {
            return new Result(counterName, 0, maxScore,
                    String.format("%s but had %d", getPrefix(), mi.getValue()));
        } else {
            return new Result(counterName, maxScore, maxScore,
                    String.format("%s and had %d.", getPrefix(), mi.getValue()));
        }
    }
}
