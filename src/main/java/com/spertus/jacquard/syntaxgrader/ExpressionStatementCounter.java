package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.spertus.jacquard.exceptions.ClientException;

import java.util.List;

/**
 * A grader to test whether the number of occurrences of the given
 * expression and statement types are in the specified numeric range.
 */
public class ExpressionStatementCounter extends SyntaxConditionCounter {
    /**
     * Creates agrader to test whether the number of occurrences of the given
     * expression and statement types are in the specified numeric range.
     *
     * @param name              the name of this grader
     * @param countedName       a brief description of the elements being counted
     * @param maxScore          the score if the condition holds
     * @param minCount          the minimum number of occurrences, which must be non-negative
     * @param maxCount          the maximum number of occurrences, or {@link Integer#MAX_VALUE}
     *                          if there is no limit
     * @param expressionClasses the expression classes
     * @param statementClasses  the statement classes
     * @throws ClientException if minCount &lt; 0, maxCount &lt; minCount,
     *                         or minCount is 0 when maxCount is {@link Integer#MAX_VALUE}
     */
    public ExpressionStatementCounter(
            String name,
            String countedName,
            double maxScore,
            int minCount,
            int maxCount,
            List<Class<? extends Expression>> expressionClasses,
            List<Class<? extends Statement>> statementClasses)
            throws ClientException {
        super(name, countedName, maxScore, minCount, maxCount,
                node -> {
                    if (node instanceof Expression) {
                        for (Class<?> clazz : expressionClasses) {
                            if (clazz.isInstance(node)) {
                                return true;
                            }
                        }
                    } else if (node instanceof Statement) {
                        for (Class<?> clazz : statementClasses) {
                            if (clazz.isInstance(node)) {
                                return true;
                            }
                        }
                    }
                    return false;
                });
    }
}
