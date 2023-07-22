package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResultTest {
    @BeforeAll
    public static void setup() {
        Autograder.initForTest();
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
        }
    }
}
