package com.spertus.jacquard;

import com.spertus.jacquard.checkstylegrader.CheckstyleGrader;
import com.spertus.jacquard.common.Result;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Tests pass only if checkstyle library is present.
public class CheckstyleGraderTest {

    @Test
    public void testCheckstyleSingleAwfulFile() throws URISyntaxException {
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", 1.0, 5);
        List<Result> results = grader.grade(List.of(
                TestUtilities.getTargetFromResource("good/BadFormatting.java")
        ));
        assertEquals(1, results.size());
        assertEquals(0, results.get(0).score()); // lots of errors
        assertEquals(5.0, results.get(0).maxScore());
    }

    @Test
    public void testCheckstyleSingleFlawedFile() throws URISyntaxException {
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", .5, 20);
        List<Result> results = grader.grade(List.of(
                TestUtilities.getTargetFromResource("good/MissingComments.java")
        ));
        assertEquals(1, results.size());
        assertEquals(18, results.get(0).score()); // 4 violations
        assertEquals(20.0, results.get(0).maxScore());
    }
}
