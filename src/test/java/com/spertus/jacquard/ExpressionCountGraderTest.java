package com.spertus.jacquard;

import com.github.javaparser.ast.expr.*;
import com.spertus.jacquard.checkstylegrader.CheckstyleGrader;
import com.spertus.jacquard.common.*;
import com.spertus.jacquard.exceptions.*;
import com.spertus.jacquard.syntaxgrader.ExpressionCountGrader;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionCountGraderTest {
    private static final String NAME = "name";
    private static final double MAX_SCORE = 10.0;

    private ExpressionCountGrader grader;
    private Target expressionTarget;

    @BeforeAll()
    public static void init() {
        Autograder.initForTest();
    }

    @BeforeEach
    public void setup() throws ClientException, URISyntaxException {
        grader = new ExpressionCountGrader(NAME, MAX_SCORE, 1, 2, InstanceOfExpr.class);
        expressionTarget = TestUtilities.getTargetFromResource("good/Expressions.java");
    }

    @Test
    public void testRepeatability() {
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", 1.0, 5);
        TestUtilities.testRepeatability(grader, expressionTarget);
    }

    @Test
    public void constructorThrowsExceptions() {
        // minimum is too low
        assertThrows(ClientException.class,
                () -> new ExpressionCountGrader(NAME, MAX_SCORE, -1, 2, SwitchExpr.class));

        // maximum is lower than minimum
        assertThrows(ClientException.class,
                () -> new ExpressionCountGrader(NAME, MAX_SCORE, 3, 2, SwitchExpr.class));

        // any count is allowed
        assertThrows(ClientException.class,
                () -> new ExpressionCountGrader(NAME, MAX_SCORE, 0, Integer.MAX_VALUE, SwitchExpr.class));
    }

    private void testHelper(
            int actualCount,
            int minCount,
            int maxCount,
            Class<? extends Expression> expressionType
    ) throws ClientException {
        ExpressionCountGrader counter = new ExpressionCountGrader(
                MAX_SCORE, minCount, maxCount, expressionType);
        List<Result> results = counter.grade(expressionTarget);
        assertEquals(1, results.size());
        assertEquals(actualCount >= minCount && actualCount <= maxCount ? MAX_SCORE : 0, results.get(0).getScore());
    }

    private void testTooFew(int actualCount, Class<? extends Expression> expressionType) throws ClientException {
        testHelper(actualCount, actualCount + 1, actualCount + 10, expressionType);
        testHelper(actualCount, actualCount + 1, Integer.MAX_VALUE, expressionType);
    }

    // actualCount must be > 0
    private void testTooMany(int actualCount, Class<? extends Expression> expressionType) throws ClientException {
        testHelper(actualCount, 0, actualCount - 1, expressionType);
    }

    private void testRightNumber(int actualCount, Class<? extends Expression> expressionType) throws ClientException {
        testHelper(actualCount, 0, actualCount, expressionType);
        testHelper(actualCount, actualCount, Integer.MAX_VALUE, expressionType);
    }

    private void testAllPossibilities(int actualCount, Class<? extends Expression> expressionType) throws ClientException {
        testTooFew(actualCount, expressionType);
        testTooMany(actualCount, expressionType);
        testRightNumber(actualCount, expressionType);
    }

    @Test
    public void testInstanceOfExprCounter() throws ClientException {
        testAllPossibilities(1, InstanceOfExpr.class);
    }

    @Test
    public void testBinaryExprCounter() throws ClientException {
        testAllPossibilities(3, BinaryExpr.class);
    }

    @Test
    public void testUnaryExprCounter() throws ClientException {
        testAllPossibilities(2, UnaryExpr.class);
    }
}
