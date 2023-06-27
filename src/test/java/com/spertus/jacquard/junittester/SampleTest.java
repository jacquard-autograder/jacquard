package com.spertus.jacquard.junittester;

import com.spertus.jacquard.common.GradedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleTest {
    @Test
    @GradedTest(name = "passingTest", points = 2.0)
    public void passingTest() {
        assertEquals(2, 1 + 1);
    }

    @Test
    @GradedTest(name = "failingTest", points = 1.5)
    public void failingTest() {
        assertEquals(3, 1 + 1);
    }

    @Test
    public void ignoredTest() {
        assertEquals(1, 1);
    }
}
