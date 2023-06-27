package com.spertus.jacquard;

import com.spertus.jacquard.crosstester.*;
import com.spertus.jacquard.common.Result;
import com.spertus.jacquard.exceptions.ClientException;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrossTesterTest {
    private static final String CSV_FILE = """
            , com.spertus.jacquard.crosstester.CorrectAdder, com.spertus.jacquard.crosstester.BuggyAdder
            addZero, 2, -1
            """;

    private static final String CSV_FILE_WITH_INT_PARAMS = String.format("""
            , %1$s#0, %1$s#1, %1$s#2, %1$s#3,
            addZero, -1, -2, -3, -4
            """, "com.spertus.jacquard.crosstester.ParameterizedBuggyAdder");

    private List<Result> getResults(String csv) throws ClientException {
        CrossTester grader = new CrossTester(GeneralizedAdderTest.class,
                new ByteArrayInputStream(csv.getBytes()));
        return grader.run();
    }

    @Test
    public void testClassesWithoutIntParameters() throws ClientException {
        List<Result> results = getResults(CSV_FILE);
        assertEquals(2, results.size());
        testResult(results.get(0), 2, 2);
        testResult(results.get(1), 1, 1);
        assertEquals(3, TestUtilities.getTotalScore(results));
    }

    private void testResult(Result result, double score, double maxScore) {
        assertEquals(score, result.score());
        assertEquals(maxScore, result.maxScore());
    }

    @Test
    public void testClassesWithIntParameters() throws ClientException {
        CrossTester grader = new CrossTester(GeneralizedAdderTest.class,
                new ByteArrayInputStream(CSV_FILE_WITH_INT_PARAMS.getBytes()));
        List<Result> results = grader.run();
        assertEquals(4, results.size());
        // Tests 0, 1, and 2 will fail. Test 3 will succeed.
        testResult(results.get(0), 1, 1);
        testResult(results.get(1), 2, 2);
        testResult(results.get(2), 3, 3);
        testResult(results.get(3), 0, 4);
    }

}
