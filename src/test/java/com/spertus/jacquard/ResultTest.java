package com.spertus.jacquard;

import com.spertus.jacquard.common.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultTest {
    private static final String NAME = "all or nothing test";
    private static final String ALL_STRING = "all passed";
    private static final String NOTHING_STRING = "not all passed";

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
        assertEquals(NAME, result.name());
        assertEquals(5.0, result.score());
        assertEquals(5.0, result.maxScore());
        assertTrue(result.output().startsWith(ALL_STRING));
        assertTrue(result.output().contains("output1"));
        assertTrue(result.output().contains("output2"));
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
        assertEquals(NAME, result.name());
        assertEquals(5.0, result.score());
        assertEquals(5.0, result.maxScore());
        assertEquals(ALL_STRING, result.output().trim());
    }

    @Test
    public void testWithSomeFailingAndOutput() {
        List<Result> results = List.of(
                Result.makeSuccess("result 1", 1.0, "output1"),
                Result.makeTotalFailure("result 2", 1.0, "output2")
        );
        Result result = Result.makeAllOrNothing(
                results,
                NAME,
                ALL_STRING,
                NOTHING_STRING,
                5.0,
                true);
        assertEquals(NAME, result.name());
        assertEquals(0.0, result.score());
        assertEquals(5.0, result.maxScore());
        assertTrue(result.output().startsWith(NOTHING_STRING));
        assertTrue(result.output().contains("output1"));
        assertTrue(result.output().contains("output2"));
    }

    @Test
    public void testWithSomeFailingWithoutOutput() {
        List<Result> results = List.of(
                Result.makeSuccess("result 1", 1.0, "output1"),
                Result.makeTotalFailure("result 2", 1.0, "output2")
        );
        Result result = Result.makeAllOrNothing(
                results,
                NAME,
                ALL_STRING,
                NOTHING_STRING,
                5.0,
                false);
        assertEquals(NAME, result.name());
        assertEquals(0.0, result.score());
        assertEquals(5.0, result.maxScore());
        assertEquals(NOTHING_STRING, result.output().trim());
    }
}
