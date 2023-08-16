package com.spertus.jacquard;

import com.spertus.jacquard.checkstylegrader.CheckstyleGrader;
import com.spertus.jacquard.common.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

public class CheckstyleGraderTest {
    @BeforeAll()
    public static void init() {
        Autograder.initForTest();
        deleteDir(new File("lib"));
    }

    // https://stackoverflow.com/a/29175213/631051
    private static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    @Test
    public void testRepeatability() throws URISyntaxException {
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", 1.0, 5);
        TestUtilities.testRepeatability(grader, "good/BadFormatting.java");
    }

    @Test
    public void testCheckstyleSingleAwfulFile() throws URISyntaxException {
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", 1.0, 5);
        List<Result> results = grader.grade( List.of(
                TestUtilities.getTargetFromResource("good/BadFormatting.java")
        ));
        // There are lots of violations.
        TestUtilities.assertResultsMatch(results, 1, 0, 5.0);
    }

    @Test
    public void testCheckstyleSingleFlawedFile() throws URISyntaxException {
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", .5, 20);
        List<Result> results = grader.grade(
                List.of(
                TestUtilities.getTargetFromResource("good/MissingComments.java")
        ));
        // There are 4 violations.
        TestUtilities.assertResultsMatch(results, 1, 18.0, 20.0);
    }
}
