package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResultMakeAllOrNothingTest {
    private static final String NAME = "all or nothing test";
    private static final String ALL_STRING = "all passed";
    private static final String NOTHING_STRING = "not all passed";

    @BeforeAll
    public static void setup() {
        Autograder.initForTest();
    }

    @Test
    public void testWithAllSucceedingAndOutput() {
        List<Result> results = List.of(
                Result.makeSuccess("result 1", 1.0, "output1"),
                Result.makeSuccess("result 2", 1.0, "output2")
        );
        Result result = Result.makeAllOrNothing(
                results,
                NAME,
                ALL_STRING,
                NOTHING_STRING,
                5.0,
                true);
        assertEquals(NAME, result.getName());
        assertEquals(5.0, result.getScore());
        assertEquals(5.0, result.getMaxScore());
        assertTrue(result.getMessage().startsWith(ALL_STRING));
        assertTrue(result.getMessage().contains("output1"));
        assertTrue(result.getMessage().contains("output2"));
    }

    @Test
    public void testWithAllSucceedingWithoutOutput() {
        List<Result> results = List.of(
                Result.makeSuccess("result 1", 1.0, "output1"),
                Result.makeSuccess("result 2", 1.0, "output2")
        );
        Result result = Result.makeAllOrNothing(
                results,
                NAME,
                ALL_STRING,
                NOTHING_STRING,
                5.0,
                false);
        assertEquals(NAME, result.getName());
        assertEquals(5.0, result.getScore());
        assertEquals(5.0, result.getMaxScore());
        assertEquals(ALL_STRING, result.getMessage().trim());
    }

    @Test
    public void testWithSomeFailingAndOutput() {
        List<Result> results = List.of(
                Result.makeSuccess("result 1", 1.0, "output1"),
                Result.makeFailure("result 2", 1.0, "output2")
        );
        Result result = Result.makeAllOrNothing(
                results,
                NAME,
                ALL_STRING,
                NOTHING_STRING,
                5.0,
                true);
        assertEquals(NAME, result.getName());
        assertEquals(0.0, result.getScore());
        assertEquals(5.0, result.getMaxScore());
        assertTrue(result.getMessage().startsWith(NOTHING_STRING));
        assertTrue(result.getMessage().contains("output1"));
        assertTrue(result.getMessage().contains("output2"));
    }

    @Test
    public void testWithSomeFailingWithoutOutput() {
        List<Result> results = List.of(
                Result.makeSuccess("result 1", 1.0, "output1"),
                Result.makeFailure("result 2", 1.0, "output2")
        );
        Result result = Result.makeAllOrNothing(
                results,
                NAME,
                ALL_STRING,
                NOTHING_STRING,
                5.0,
                false);
        assertEquals(NAME, result.getName());
        assertEquals(0.0, result.getScore());
        assertEquals(5.0, result.getMaxScore());
        assertEquals(NOTHING_STRING, result.getMessage().trim());
    }

    @Test
    public void testNoResults() {
        assertThrows(IllegalArgumentException.class,
                () -> Result.makeAllOrNothing(List.of(), NAME, ALL_STRING, NOTHING_STRING, 1.0, false));
    }

    @Test
    public void testDifferentVisibilities() {
        List<Result> results = List.of(
                Result.makeSuccess("result 1", 1.0, "output1", Visibility.HIDDEN),
                Result.makeFailure("result 2", 1.0, "output2", Visibility.VISIBLE)
        );
        assertThrows(IllegalArgumentException.class,
                () -> Result.makeAllOrNothing(results, NAME, ALL_STRING, NOTHING_STRING, 1.0, false));
    }

    @Test
    public void testResultVisibility() {
        List<Result> results = List.of(
                Result.makeSuccess("result 1", 1.0, "output1", Visibility.HIDDEN),
                Result.makeFailure("result 2", 1.0, "output2", Visibility.HIDDEN)
        );
        Result result = Result.makeAllOrNothing(results, NAME, ALL_STRING, NOTHING_STRING, 1.0, false);
        assertEquals(Visibility.HIDDEN, result.getVisibility());
    }

}
