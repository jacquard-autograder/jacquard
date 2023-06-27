package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.junittester.SampleTest;
import com.spertus.jacquard.junittester.JUnitTester;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JUnitTesterTest {
    private static final String PASSING_TEST_NAME = "passingTest";
    private static final double PASSING_TEST_MAX_POINTS = 2.0;
    private static final String FAILING_TEST_NAME = "failingTest";
    private static final double FAILING_TEST_MAX_POINTS = 1.5;

    private void checkResultsHelper(Result passingResult, Result failingResult) {
        assertEquals(PASSING_TEST_NAME, passingResult.name());
        assertEquals(PASSING_TEST_MAX_POINTS, passingResult.maxScore());
        assertEquals(PASSING_TEST_MAX_POINTS, passingResult.score());
        assertEquals(FAILING_TEST_NAME, failingResult.name());
        assertEquals(FAILING_TEST_MAX_POINTS, failingResult.maxScore());
        assertEquals(0, failingResult.score());
    }

    private void checkResults(Tester tester) {
        List<Result> results = tester.run();
        assertEquals(2, results.size());
        Result result1 = results.get(0);
        Result result2 = results.get(1);
        if (result1.name().equals(PASSING_TEST_NAME)) {
            checkResultsHelper(result1, result2);
        } else {
            checkResultsHelper(result2, result1);
        }
    }

    @Test
    public void testClass() {
        JUnitTester tester = new JUnitTester(SampleTest.class);
        checkResults(tester);
    }

    @Test
    public void testPackage() {
        JUnitTester tester = new JUnitTester("com.spertus.jacquard.junittester");
        checkResults(tester);
    }
}
