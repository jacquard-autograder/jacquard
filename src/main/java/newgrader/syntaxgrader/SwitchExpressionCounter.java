package newgrader.syntaxgrader;

import com.github.javaparser.ast.expr.SwitchExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Counter of number of occurrences of switch expressions.
 * <p>
 * This class is not really necessary, since {@link ExpressionCounter}
 * could provide the same functionality.
 */
public class SwitchExpressionCounter extends Counter {
    /**
     * Create a new switch expression counter.
     *
     * @param maxScore the score if the condition holds
     * @param minCount the minimum number of occurrences
     * @param maxCount the maximum number of occurrences, or {@link Integer#MAX_VALUE}
     *                 if there is no limit
     * @throws IllegalArgumentException if minCount &lt; 0, maxCount &lt; minCount,
     *                                  or minCount is 0 when maxCount is {@link Integer#MAX_VALUE}
     */
    public SwitchExpressionCounter(double maxScore, int minCount, int maxCount) {
        super("switch expression counter", "switch expression", maxScore, minCount, maxCount,
                new VoidVisitorAdapter<>() {
                    @Override
                    public void visit(SwitchExpr node, MutableInteger mi) {
                        mi.increment();
                    }
                });
    }
}
