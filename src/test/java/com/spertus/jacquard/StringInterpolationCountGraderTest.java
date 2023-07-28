package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.syntaxgrader.*;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

public class StringInterpolationCountGraderTest {
    StringInterpolationCountGrader grader;
    Target mobTarget;

    @BeforeEach
    public void setup() throws URISyntaxException {
        Autograder.initForTest();
        grader = new StringInterpolationCountGrader("String interpolation grader", 1, 2, Integer.MAX_VALUE);
        mobTarget = TestUtilities.getTargetFromResource("good/Mob.java");
    }

    @Test
    public void testRepeatability() {
        TestUtilities.testTwice(grader, mobTarget);
    }

    @Test
    public void counterTest() {
        List<Result> results = grader.grade(mobTarget);
        TestUtilities.assertResultsMatch(results, 1, 1.0, 1.0);
    }
}
