package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.Node;
import com.spertus.jacquard.exceptions.ClientException;

import java.util.function.Predicate;

/**
 * A grader to test whether exactly the specified number of parse nodes
 * satisfy a predicate.
 */
public class SyntaxConditionGrader extends SyntaxConditionCountGrader {
    private static String GRADER_FORMAT_STRING = "%s checker";

    /**
     * Creates a grader to test whether the specified number of parse nodes
     * satisfy the predicate.
     *
     * @param name        the name of this grader
     * @param count       the desired count,
     * @param countedName the name of the element being checked
     * @param maxScore    the score if the condition holds
     * @param predicate   the condition
     */
    public SyntaxConditionGrader(
            final String name,
            final int count,
            final String countedName,
            final double maxScore,
            final Predicate<Node> predicate)
            throws ClientException {
        super(name, countedName, maxScore, count, count, predicate);
    }

    /**
     * Creates a grader with a default name to test whether any parse nodes
     * satisfy the predicate.
     *
     * @param count       the desired count,
     * @param countedName the name of the element being checked
     * @param maxScore    the score if the condition holds
     * @param predicate   the condition
     */
    public SyntaxConditionGrader(
            final int count,
            final String countedName,
            final double maxScore,
            final Predicate<Node> predicate)
            throws ClientException {
        this(String.format(GRADER_FORMAT_STRING, countedName),
                count,
                countedName,
                maxScore,
                predicate);
    }
}
