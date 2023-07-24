package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.Node;
import com.spertus.jacquard.exceptions.ClientException;

import java.util.function.Predicate;

/**
 * A grader to test whether exactly one parse node satisfies a
 * given predicate.
 */
public class SyntaxConditionGrader extends SyntaxConditionCountGrader {
    /**
     * Creates a new counter to test whether the number of parse nodes
     * satisfying the predicate is within the specified range.
     *
     * @param name        the name of this grader
     * @param countedName the name of the element being checked
     * @param maxScore    the score if the condition holds
     * @param predicate   the condition
     */
    public SyntaxConditionGrader(
            String name,
            String countedName,
            double maxScore,
            Predicate<Node> predicate)
            throws ClientException {
        super(name, countedName, maxScore, 1, 1, predicate);
    }
}
