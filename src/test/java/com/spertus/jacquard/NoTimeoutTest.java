package com.spertus.jacquard;

import com.spertus.jacquard.checkstylegrader.CheckstyleGrader;
import com.spertus.jacquard.common.*;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NoTimeoutTest {
    @Test
    public void testNoTimeout() throws URISyntaxException {
        Autograder.resetForTest();
        Autograder.Builder.getInstance().timeout(0).build(); // no timeout
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", .5, 20);
        List<Result> results = grader.grade(
                List.of(
                        TestUtilities.getTargetFromResource("good/MissingComments.java")
                ));
        assertEquals(1, results.size());
        assertEquals(18, results.get(0).getScore()); // 4 violations
        assertEquals(20.0, results.get(0).getMaxScore());
    }
}
