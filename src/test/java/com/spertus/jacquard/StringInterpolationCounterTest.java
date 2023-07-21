package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.syntaxgrader.*;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringInterpolationCounterTest {
    private Autograder autograder = new Autograder();

    @Test
    public void counterTest() throws URISyntaxException {
        SyntaxCounter counter = new StringInterpolationCounter("String interpolation counter", 1, 2, Integer.MAX_VALUE);
        Target target = TestUtilities.getTargetFromResource("good/Mob.java");
        List<Result> results = autograder.grade(counter, target);
        assertEquals(1, results.size());
        assertEquals(1.0, TestUtilities.getTotalScore(results));
    }
}
