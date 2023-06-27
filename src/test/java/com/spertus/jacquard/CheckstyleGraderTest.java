package com.spertus.jacquard;

import com.spertus.jacquard.checkstylegrader.CheckstyleGrader;
import com.spertus.jacquard.common.Result;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckstyleGraderTest {

    @Test
    public void testCheckstyleSingleFile() throws URISyntaxException {
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", 0, 5);
        List<Result> results = grader.grade(List.of(
                TestUtilities.getTargetFromResource("good/BadFormatting.java")
        ));
        assertEquals(1, results.size());
        assertEquals(0, results.get(0).score()); // lots of errors
        assertEquals(5.0, results.get(0).maxScore());
        System.out.println(results);
    }
}
