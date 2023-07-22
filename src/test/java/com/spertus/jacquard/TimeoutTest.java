package com.spertus.jacquard;

import com.spertus.jacquard.checkstylegrader.CheckstyleGrader;
import com.spertus.jacquard.common.*;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimeoutTest {
    @Test
    public void testTimeout() throws URISyntaxException {
        Autograder.resetForTest();
        Autograder.Builder.getInstance().timeout(1).build(); // 1 ms timeout
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", .5, 20);
        List<Result> results = grader.grade(
                List.of(
                        TestUtilities.getTargetFromResource("good/MissingComments.java")
                ));
        assertEquals(1, results.size());
        assertEquals(0, results.get(0).getScore());
        assertTrue(results.get(0).getMessage().contains("time"));
    }
}
