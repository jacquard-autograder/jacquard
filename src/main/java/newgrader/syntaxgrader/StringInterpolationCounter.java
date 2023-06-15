package newgrader.syntaxgrader;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import newgrader.common.Result;
import newgrader.exceptions.ClientException;

/**
 * Counter of number of occurrences of string interpolation.
 */
public class StringInterpolationCounter extends Counter {
    /**
     * Create a new string interpolation counter.
     *
     * @param name     the name of this processor (for the {@link Result})
     * @param maxScore the score if the condition holds
     * @param minCount the minimum number of occurrences
     * @param maxCount the maximum number of occurrences, or {@link Integer#MAX_VALUE}
     *                 if there is no limit
     * @throws ClientException if minCount &lt; 0, maxCount &lt; minCount,
     *                         or minCount is 0 when maxCount is {@link Integer#MAX_VALUE}
     */
    public StringInterpolationCounter(String name, int maxScore, int minCount, int maxCount)
            throws ClientException {
        super(name, "string interpolations", maxScore, minCount, maxCount, new StringInterpolationAdapter());
    }

    private static class StringInterpolationAdapter extends VoidVisitorAdapter<MutableInteger> {
        @Override
        public void visit(MethodCallExpr node, MutableInteger mi) {
            if (node.getScope().isPresent()) {
                final String fullMethodName = node.getScope().get() + "." + node.getNameAsString();
                if (("System.out.printf".equals(fullMethodName) || "String.format".equals(fullMethodName))
                        && node.getArguments().size() > 1) {
                    mi.increment();
                }
            }
        }
    }
}
