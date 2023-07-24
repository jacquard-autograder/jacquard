package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.spertus.jacquard.common.Result;
import com.spertus.jacquard.exceptions.ClientException;

/**
 * A grader that counts the number of occurrences of string interpolation.
 */
public class StringInterpolationCountGrader extends SyntaxCountGrader {
    private static final String GRADER_NAME = "string interpolation counter";

    /**
     * Create a new string interpolation count grader.
     *
     * @param name     the name of this processor (for the {@link Result})
     * @param maxScore the score if the condition holds
     * @param minCount the minimum number of occurrences
     * @param maxCount the maximum number of occurrences, or {@link Integer#MAX_VALUE}
     *                 if there is no limit
     * @throws ClientException if minCount &lt; 0, maxCount &lt; minCount,
     *                         or minCount is 0 when maxCount is {@link Integer#MAX_VALUE}
     */
    public StringInterpolationCountGrader(String name, int maxScore, int minCount, int maxCount)
            throws ClientException {
        super(name, "string interpolations", maxScore, minCount, maxCount, new StringInterpolationAdapter());
    }

    /**
     * Create a new string interpolation count grader with the default name.
     *
     * @param maxScore the score if the condition holds
     * @param minCount the minimum number of occurrences
     * @param maxCount the maximum number of occurrences, or {@link Integer#MAX_VALUE}
     *                 if there is no limit
     * @throws ClientException if minCount &lt; 0, maxCount &lt; minCount,
     *                         or minCount is 0 when maxCount is {@link Integer#MAX_VALUE}
     */
    public StringInterpolationCountGrader(int maxScore, int minCount, int maxCount)
            throws ClientException {
        this(GRADER_NAME, maxScore, minCount, maxCount);
    }

    private static class StringInterpolationAdapter extends VoidVisitorAdapter<MutableInteger> { // NOPMD
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
