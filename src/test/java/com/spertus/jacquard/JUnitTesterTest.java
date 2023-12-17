package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.junittester.SampleTest;
import com.spertus.jacquard.junittester.JUnitTester;
import com.spertus.jacquard.junittester.group.GroupTest;
import com.spertus.jacquard.junittester.visibility.VisibilityLevelsTest;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JUnitTesterTest {
    private static final String PASSING_TEST_NAME = "passingTest";
    private static final double PASSING_TEST_MAX_POINTS = 2.0;
    private static final String FAILING_TEST_NAME = "failingTest";
    private static final double FAILING_TEST_MAX_POINTS = 1.5;

    @BeforeAll()
    public static void init() {
        Autograder.initForTest();
    }

    private void checkResultsHelper(Result passingResult, Result failingResult) {
        assertEquals(PASSING_TEST_NAME, passingResult.getName());
        assertEquals(PASSING_TEST_MAX_POINTS, passingResult.getMaxScore());
        assertEquals(PASSING_TEST_MAX_POINTS, passingResult.getScore());
        assertEquals(Visibility.HIDDEN, passingResult.getVisibility());
        assertEquals(FAILING_TEST_NAME, failingResult.getName());
        assertEquals(FAILING_TEST_MAX_POINTS, failingResult.getMaxScore());
        assertEquals(0, failingResult.getScore());
        assertEquals(Visibility.VISIBLE, failingResult.getVisibility());
    }

    private void checkResults(Tester tester) {
        List<Result> results = tester.run();
        assertEquals(2, results.size());
        Result result1 = results.get(0);
        Result result2 = results.get(1);
        if (result1.getName().equals(PASSING_TEST_NAME)) {
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
    public void testPackageExcludingSubpackages() {
        JUnitTester tester = new JUnitTester("com.spertus.jacquard.junittester", false);
        checkResults(tester);
    }

    @Test
    public void testPackageIncludingSubpackages() {
        JUnitTester tester = new JUnitTester("com.spertus.jacquard.junittester", true);
        List<Result> results = tester.run();
        assertEquals(9, results.size());
    }

    @Test
    public void testVisibility() {
        JUnitTester tester = new JUnitTester(VisibilityLevelsTest.class);
        List<Result> results = tester.run();

        for (Result result : results) {
            Visibility expectedVisibility = switch (result.getName()) {
                case "visibleTest1()", "visibleTest2()" -> Visibility.VISIBLE;
                case "afterDueDateTest()" -> Visibility.AFTER_DUE_DATE;
                case "afterPublishedTest()" -> Visibility.AFTER_PUBLISHED;
                case "hiddenTest()" -> Visibility.HIDDEN;
                default ->
                        throw new AssertionError("Test had unexpected name: " + result.getName());
            };
            assertEquals(expectedVisibility, result.getVisibility(),
                    "Visibility incorrect for " + result.getName());
        }
    }

    @Test
    public void testGrouping() {
        JUnitTester tester = new JUnitTester(GroupTest.class);
        List<Result> results = tester.run();
        assertEquals(1, results.size());
    }
}
