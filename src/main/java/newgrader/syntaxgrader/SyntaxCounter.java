package newgrader.syntaxgrader;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import newgrader.common.Result;
import newgrader.exceptions.ClientException;

import java.util.List;

/**
 * The base class for counters to test whether the number of occurrences of a
 * syntactic element is within the specified range.
 */
public abstract class SyntaxCounter implements SyntaxGrader {
    private final String counterName;
    private final String countedName;
    private final double maxScore;
    private final int minCount;
    private final int maxCount;
    private final VoidVisitorAdapter<MutableInteger> adapter;

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
     * @throws ClientException if minCount &lt; 0, maxCount &lt; minCount,
     *                         or minCount is 0 when maxCount is {@link Integer#MAX_VALUE}
     */
    public SyntaxCounter(
            String counterName,
            String countedName,
            double maxScore,
            int minCount,
            int maxCount,
            VoidVisitorAdapter<MutableInteger> adapter
    ) throws ClientException {
        if (minCount < 0) {
            throw new ClientException("minCount must be >= 0");
        }
        if (maxCount < minCount) {
            throw new ClientException("maxCount must be >= minCount");
        }
        if (minCount == 0 && maxCount == Integer.MAX_VALUE) {
            throw new ClientException(
                    "There is no reason to create a SyntaxCounter of 0 or more elements");
        }

        this.counterName = counterName;
        this.countedName = countedName;
        this.maxScore = maxScore;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.adapter = adapter;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The returned list will always have a single element.
     */
    @Override
    public List<Result> grade(CompilationUnit cu) {
        final MutableInteger mi = new MutableInteger();
        adapter.visit(cu, mi);
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

    private Result getResult(MutableInteger mi) {
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
