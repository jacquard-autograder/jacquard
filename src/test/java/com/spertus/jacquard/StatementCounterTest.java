package com.spertus.jacquard;

import com.github.javaparser.ast.stmt.*;
import com.spertus.jacquard.common.*;
import com.spertus.jacquard.exceptions.ClientException;
import com.spertus.jacquard.syntaxgrader.StatementCounter;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatementCounterTest {
    private static final double MAX_SCORE = 2.5;
    private static Target forTarget;

    @BeforeAll
    public static void setup() throws URISyntaxException {
        Autograder.initForTest();
        forTarget = TestUtilities.getTargetFromResource("good/ForStatements.java");
    }

    private void testHelper(int actualCount, int minCount, int maxCount, Class<? extends Statement> statementType)
            throws ClientException {
        StatementCounter counter = new StatementCounter(
                MAX_SCORE, minCount, maxCount, statementType);
        List<Result> results = counter.grade(forTarget);
        assertEquals(1, results.size());
        assertEquals(actualCount >= minCount && actualCount <= maxCount ? MAX_SCORE : 0, results.get(0).getScore());
    }

    private void testTooFew(int actualCount, Class<? extends Statement> statementType) throws ClientException {
        testHelper(actualCount, actualCount + 1, actualCount + 10, statementType);
        testHelper(actualCount, actualCount + 1, Integer.MAX_VALUE, statementType);
    }

    // actualCount must be > 0
    private void testTooMany(int actualCount, Class<? extends Statement> statementType) throws ClientException {
        testHelper(actualCount, 0, actualCount - 1, statementType);
    }

    private void testRightNumber(int actualCount, Class<? extends Statement> statementType) throws ClientException {
        testHelper(actualCount, 0, actualCount, statementType);
        testHelper(actualCount, actualCount, Integer.MAX_VALUE, statementType);
    }

    private void testAllPossibilities(int actualCount, Class<? extends Statement> statementType) throws ClientException {
        testTooFew(actualCount, statementType);
        testTooMany(actualCount, statementType);
        testRightNumber(actualCount, statementType);
    }

    @Test
    public void testForStatementCounter() throws ClientException {
        testAllPossibilities(1, ForStmt.class);
    }

    @Test
    public void testAssignmentStatementCounter() throws ClientException {
        testAllPossibilities(2, ForEachStmt.class);
    }
}
