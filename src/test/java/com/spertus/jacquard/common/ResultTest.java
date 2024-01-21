package com.spertus.jacquard.common;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResultTest {
    private Result rBigSuccess;
    private Result rHugeFailure;
    private Result rMixed;
    private List<Result> results;

    @BeforeEach
    public void setup() {
        Autograder.initForTest();
        rBigSuccess = Result.makeSuccess("big success", 10, "big success message");
        rHugeFailure = Result.makeFailure("huge Failure", 15, "huge failure message");
        rMixed = Result.makeResult("mixed success", 3.0, 6.0, "mixed success message");
        results = List.of(rBigSuccess, rHugeFailure, rMixed);
    }

    @Test
    public void testDefaultVisibility() {
        Result result = Result.makeSuccess("test", 5, "message");
        assertEquals(Autograder.Builder.DEFAULT_VISIBILITY, result.getVisibility());
    }

    @Test
    public void testHiddenVisibility() {
        Result result = Result.makeSuccess("test", 5, "message", Visibility.HIDDEN);
        assertEquals(Visibility.HIDDEN, result.getVisibility());
    }

    @Test
    public void testChangedDefaultVisibility() {
        for (Visibility v : Visibility.values()) {
            Autograder.resetForTest();
            Autograder.Builder.getInstance().visibility(v).build();
            Result result = Result.makeSuccess("test", 5, "message");
            assertEquals(v, result.getVisibility());
            Autograder.resetForTest(); // keep from affecting downstream tests
        }
    }

    @Test
    public void testReorderNaturally() {
        assertEquals(
                List.of(rBigSuccess, rHugeFailure, rMixed),
                Result.reorderResults(results, Result.Order.NATURAL)
        );
    }

    @Test
    public void testReorderAlphabetically() {
        assertEquals(List.of(rBigSuccess, rHugeFailure, rMixed), Result.reorderResults(results, Result.Order.ALPHABETICAL));
    }

    @Test
    public void testReorderDecreasingMaxScore() {
        assertEquals(List.of(rHugeFailure, rBigSuccess, rMixed), Result.reorderResults(results, Result.Order.DECREASING_MAX_SCORE));
    }

    @Test
    public void testReorderIncreasingMaxScore() {
        assertEquals(List.of(rMixed, rBigSuccess, rHugeFailure), Result.reorderResults(results, Result.Order.INCREASING_MAX_SCORE));
    }

    @Test
    public void testTrimMessage() {
        String message = "Hello, world!";
        assertEquals(message, Result.trimMessage(message, message.length(), "..."));
        assertEquals(
                "Hello, wo...",
                Result.trimMessage(message, message.length() - 1, "...")
        );
        assertEquals(
                "Hello!",
                Result.trimMessage(message, 6, "!")
        );
    }
}
