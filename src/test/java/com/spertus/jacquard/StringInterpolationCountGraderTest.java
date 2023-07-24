package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.syntaxgrader.*;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringInterpolationCountGraderTest {
    @BeforeEach
    public void setup() {
        Autograder.initForTest();
    }

    @Test
    public void counterTest() throws URISyntaxException {
        SyntaxCountGrader counter = new StringInterpolationCountGrader("String interpolation counter", 1, 2, Integer.MAX_VALUE);
        Target target = TestUtilities.getTargetFromResource("good/Mob.java");
        List<Result> results = counter.grade(target);
        assertEquals(1, results.size());
        assertEquals(1.0, TestUtilities.getTotalScore(results));
    }
}
