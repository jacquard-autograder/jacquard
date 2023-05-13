package newgrader;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Preconditions;

import java.util.List;

public abstract class Counter implements Processor {
    private final String counterName;
    private final String countedName;
    private final double maxScore;
    private final int minCount;
    private final int maxCount;

    protected VoidVisitorAdapter<MutableInteger> adapter;

    public Counter(String counterName, String countedName, double maxScore, int minCount, int maxCount) {
        Preconditions.checkArgument(minCount >= 0);
        Preconditions.checkArgument(maxCount >= minCount);
        // It makes no sense to have minCount be 0 when MaxCount is Integer.MAX_VALUE.
        Preconditions.checkState(minCount > 0 || maxCount < Integer.MAX_VALUE);

        this.counterName = counterName;
        this.countedName = countedName;
        this.maxScore = maxScore;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    @Override
    public List<Result> process(CompilationUnit cu) {
        MutableInteger mi = new MutableInteger();
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

    public Result getResult(MutableInteger mi) {
        if (mi.getValue() < minCount) {
            return new Result(counterName, 0, maxScore,
                    String.format("%s but had only %d", getPrefix(), mi.getValue()));
        }
        else if (mi.getValue() > maxCount) {
            return new Result(counterName, 0, maxScore,
                    String.format("%s but had %d", getPrefix(), mi.getValue()));
        }
        else {
            return new Result(counterName, maxScore, maxScore,
                    String.format("%s and had %d.", getPrefix(), mi.getValue()));
        }
    }
}
