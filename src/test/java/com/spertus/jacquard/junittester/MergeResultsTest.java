package com.spertus.jacquard.junittester;

import com.google.common.collect.Sets;
import com.spertus.jacquard.common.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MergeResultsTest {
    public static final String NAME = "name";
    private static Result result1;
    private static Result result2;
    private static Result result3;

    @BeforeEach
    public void setup() {
        Autograder.initForTest();
        result1 = Result.makeSuccess(NAME, 1.0, "message1");
        result2 = Result.makeFailure(NAME, 2.0, "message2");
        result3 = Result.makeResult(NAME, 3.5, 4.0, "message3");
    }

    @Test
    public void testMerge0() {
        assertThrows(IllegalArgumentException.class, () -> JUnitTester.mergeResults(List.of()));
    }

    @Test
    public void testMergeDifferentNames() {
        List<Result> mismatchedResults = List.of(
                result1,
                Result.makeFailure("different name", 5.0, "ignored message")
        );
        assertThrows(IllegalArgumentException.class, () -> JUnitTester.mergeResults(mismatchedResults));
    }

    // Checks if strings have same lines, possibly in different order.
    private void assertSameLines(String s1, String s2) {
        Set<String> lines1 = Sets.newHashSet(s1.split("\n"));
        Set<String> lines2 = Sets.newHashSet(s2.split("\n"));
        if (lines1.equals(lines2)) {
            return;
        }
        // This provides better messages than just failing.
        for (String s : lines1) {
            assertTrue(lines2.contains(s));
        }
        for (String s : lines2) {
            assertTrue(lines1.contains(s));
        }
    }

    // Checks if all resultLists produce results that match the arguments.
    @SafeVarargs
    private void assertMatching(
            String name,
            double score,
            double maxScore,
            String message,
            List<Result>... resultLists
    ) {
        for (List<Result> resultList : resultLists) {
            Result result = JUnitTester.mergeResults(resultList);
            assertEquals(name, result.getName());
            assertEquals(score, result.getScore());
            assertEquals(maxScore, result.getMaxScore());
            assertSameLines(message, result.getMessage());
        }
    }

    // single results don't get merged
    @Test
    public void testMerge1() {
        // Success
        assertMatching(NAME, 1.0, 1.0, "message1", List.of(result1));

        // Failure
        assertMatching(NAME, 0.0, 2.0, "message2", List.of(result2));

        // Partial success
        assertMatching(NAME, 3.5, 4.0, "message3", List.of(result3));
    }

    @Test
    public void testMerge2() {
        // One success, one failure
        assertMatching(NAME, 1.0, 3.0, "-2.0: message2",
                List.of(result1, result2),
                List.of(result2, result1));

        // One success, one partial
        assertMatching(NAME, 4.5, 5.0, "-0.5: message3",
                List.of(result1, result3),
                List.of(result3, result1));

        // One partial, one failure
        assertMatching(NAME, 3.5, 6.0, "-2.0: message2\n-0.5: message3",
                List.of(result2, result3), List.of(result3, result2));
    }

    @Test
    public void testMerge3() {
        assertMatching(NAME, 4.5, 7.0, "-2.0: message2\n-0.5: message3",
                List.of(result1, result2, result3),
                List.of(result1, result3, result2),
                List.of(result2, result1, result3),
                List.of(result2, result3, result1),
                List.of(result3, result1, result2),
                List.of(result3, result2, result1));
    }
}
