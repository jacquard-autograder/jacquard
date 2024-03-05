package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.junittester.SampleTest;
import com.spertus.jacquard.junittester.JUnitTester;
import com.spertus.jacquard.junittester.group.GroupTest1;
import com.spertus.jacquard.junittester.group.GroupTest2;
import com.spertus.jacquard.junittester.output.OutputTest;
import com.spertus.jacquard.junittester.visibility.VisibilityLevelsTest;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        // There should be:
        // 1 from GroupTest1
        // 2 from GroupTest2
        // 2 from OutputTest
        // 2 results from SampleTest
        // 5 results from VisibilityTest
        assertEquals(12, results.size());
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
    public void testGroupingWithSameVisibility() {
        JUnitTester tester = new JUnitTester(GroupTest1.class);
        List<Result> results = tester.run();
        assertEquals(1, results.size());
    }

    @Test
    public void testGroupingWithDifferentVisibility() {
        JUnitTester tester = new JUnitTester(GroupTest2.class);
        List<Result> results = tester.run();
        assertEquals(2, results.size());
        Map<Visibility, List<Result>> map = results.stream().collect(Collectors.groupingBy(Result::getVisibility));
        assertEquals(2, map.size());
        assertTrue(map.containsKey(Visibility.VISIBLE));
        assertEquals(1, map.get(Visibility.VISIBLE).size());
        assertEquals(1.0, map.get(Visibility.VISIBLE).get(0).getMaxScore());
        assertEquals(1, map.get(Visibility.AFTER_PUBLISHED).size());
        assertEquals(2.0, map.get(Visibility.AFTER_PUBLISHED).get(0).getMaxScore());
    }

    @Test
    public void testOutput() {
        JUnitTester tester = new JUnitTester(OutputTest.class);
        List<Result> results = tester.run();
        assertEquals(2, results.size());
        // The test with name1, description1 should have no output included.
        Result result1 = results.get(0).getName().equals("name1") ? results.get(0) : results.get(1);
        Result result2 = result1.equals(results.get(0)) ? results.get(1) : results.get(0);
        assertEquals("description1", result1.getMessage());
        assertEquals("description2\nOUTPUT\n======\noutput2", result2.getMessage());
    }
}
