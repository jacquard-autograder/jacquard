package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.spertus.jacquard.common.Result;
import com.spertus.jacquard.exceptions.ClientException;

import java.util.*;

/**
 * The base class for counters to test whether the number of occurrences of a
 * syntactic element is within the specified range.
 */
public abstract class SyntaxCounter extends SyntaxGrader {
    private final String countedName;
    private final double maxScore;
    private final int minCount;
    private final int maxCount;
    private final VoidVisitorAdapter<MutableInteger> adapter;

    /**
     * Creates a new counter to test whether the number of occurrences of an
     * element is within the specified range.
     *
     * @param name        the name of this processor (for the {@link Result})
     * @param countedName the name of the element (for the {@link Result})
     * @param maxScore    the score if the condition holds
     * @param minCount    the minimum number of occurrences
     * @param maxCount    the maximum number of occurrences, or {@link Integer#MAX_VALUE}
     *                    if there is no limit.
     * @param adapter     an adapter for visiting nodes in parse tree
     * @throws ClientException if minCount &lt; 0, maxCount &lt; minCount,
     *                         or minCount is 0 when maxCount is {@link Integer#MAX_VALUE}
     */
    public SyntaxCounter(
            final String name,
            final String countedName,
            final double maxScore,
            final int minCount,
            final int maxCount,
            final VoidVisitorAdapter<MutableInteger> adapter
    ) throws ClientException {
        super(name);
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

        this.countedName = countedName;
        this.maxScore = maxScore;
        this.minCount = minCount;
        this.maxCount = maxCount;
        Objects.requireNonNull(adapter);
        this.adapter = adapter;
    }

    @Override
    protected List<Result> grade(final CompilationUnit cu) {
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

        if (minCount == maxCount) {
            return String.format("Code was required to have %d %s", minCount, countedName);
        }

        return String.format("Code was required to have %d-%d %s", minCount, maxCount, countedName);
    }

    /**
     * Creates a result with an appropriate score and message based on the
     * number of occurrences of the syntactic element.
     *
     * @param mi the number of occurrences of the syntactic element.
     * @return the result
     */
    protected Result getResult(final MutableInteger mi) {
        if (mi.getValue() < minCount) {
            return makeFailureResult(maxScore,
                    String.format("%s but had %d", getPrefix(), mi.getValue()));
        } else if (mi.getValue() > maxCount) {
            return makeFailureResult(maxScore,
                    String.format("%s but had %d", getPrefix(), mi.getValue()));
        } else {
            return makeSuccessResult(maxScore,
                    String.format("%s and had %d.", getPrefix(), mi.getValue()));
        }
    }
}
