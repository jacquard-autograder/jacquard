package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.expr.*;
import com.spertus.jacquard.common.Result;
import com.spertus.jacquard.exceptions.ClientException;

import java.util.List;

/**
 * A counter to test whether the number of occurrences of a given expression
 * type is within the specified range.
 */
public class ExpressionCounter extends ExpressionStatementCounter {

    /**
     * Creates a new counter to test whether the number of occurrences of an
     * expression type is within the specified range.
     *
     * @param name     the name of this processor (for the {@link Result})
     * @param maxScore the score if the condition holds
     * @param minCount the minimum number of occurrences
     * @param maxCount the maximum number of occurrences, or {@link Integer#MAX_VALUE}
     *                 if there is no limit
     * @param clazz    the expression class
     * @throws ClientException if minCount &lt; 0, maxCount &lt; minCount,
     *                                              or minCount is 0 when maxCount is {@link Integer#MAX_VALUE}
     */
    public ExpressionCounter(String name, double maxScore, int minCount, int maxCount, Class<? extends Expression> clazz)
            throws ClientException {
        super(name, clazz.getSimpleName(), maxScore, minCount, maxCount, List.of(clazz), List.of());
    }
}
