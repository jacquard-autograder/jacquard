package com.spertus.jacquard;

import com.spertus.jacquard.checkstylegrader.CheckstyleGrader;
import com.spertus.jacquard.common.*;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Tests pass only if checkstyle library is present.
public class CheckstyleGraderTest {
    Autograder autograder = new Autograder();

    @Test
    public void testCheckstyleSingleAwfulFile() throws URISyntaxException {
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", 1.0, 5);
        List<Result> results = autograder.grade(grader, List.of(
                TestUtilities.getTargetFromResource("good/BadFormatting.java")
        ));
        assertEquals(1, results.size());
        assertEquals(0, results.get(0).getScore()); // lots of errors
        assertEquals(5.0, results.get(0).getMaxScore());
    }

    @Test
    public void testCheckstyleSingleFlawedFile() throws URISyntaxException {
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", .5, 20);
        List<Result> results = autograder.grade(grader,
                List.of(
                TestUtilities.getTargetFromResource("good/MissingComments.java")
        ));
        assertEquals(1, results.size());
        assertEquals(18, results.get(0).getScore()); // 4 violations
        assertEquals(20.0, results.get(0).getMaxScore());
    }


    @Test
    public void testTimeout() throws URISyntaxException {
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", .5, 20);
        Autograder auto = new Autograder();
        auto.setTimeout(1); // 1 ms timeout
        List<Result> results = auto.grade(grader,
                List.of(
                        TestUtilities.getTargetFromResource("good/MissingComments.java")
                ));
        assertEquals(1, results.size());
        assertEquals(0, results.get(0).getScore());
        assertTrue(results.get(0).getMessage().contains("time"));
    }

    @Test
    public void testNoTimeout() throws URISyntaxException {
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", .5, 20);
        Autograder auto = new Autograder();
        auto.setTimeout(0); // no timeout
        List<Result> results = auto.grade(grader,
                List.of(
                        TestUtilities.getTargetFromResource("good/MissingComments.java")
                ));
        assertEquals(1, results.size());
        assertEquals(18, results.get(0).getScore()); // 4 violations
        assertEquals(20.0, results.get(0).getMaxScore());
    }
}
