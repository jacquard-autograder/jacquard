package com.spertus.jacquard;

import com.spertus.jacquard.checkstylegrader.CheckstyleGrader;
import com.spertus.jacquard.common.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.*;
import java.util.List;

public class CheckstyleGraderTest {
    private static Target badFormattingTarget;
    private static Target missingCommentsTarget;

    @BeforeAll()
    public static void init() throws URISyntaxException {
        Autograder.initForTest();
        // The next line makes this test slow but tests that the library is
        // downloaded if needed.
        deleteDir(new File("lib"));
        badFormattingTarget = TestUtilities.getTargetFromResource("good/BadFormatting.java");
        missingCommentsTarget = TestUtilities.getTargetFromResource("good/MissingComments.java");
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
    public void testCheckstyleSingleAwfulFile() {
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", 1.0, 20.0);
        List<Result> results = grader.grade(badFormattingTarget);
        // There are 8 violations.
        TestUtilities.assertResultsMatch(results, 1, 12.0, 20.0);
    }

    @Test
    public void testCheckstyleSingleFlawedFile() {
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", .5, 20);
        List<Result> results = grader.grade(missingCommentsTarget);
        // There are 4 violations.
        TestUtilities.assertResultsMatch(results, 1, 18.0, 20.0);
    }

    @Test
    public void testCheckstyleMultipleFiles() throws URISyntaxException {
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", 1.0, 20.0);
        List<Result> results = grader.grade(badFormattingTarget, missingCommentsTarget);
        // There are 11 violations (because package-info violation counted only once).
        TestUtilities.assertResultsMatch(results, 1, 9.0, 20.0);
    }

    @Test
    public void testCheckstyleMultipleFilesWithList() throws URISyntaxException {
        CheckstyleGrader grader = new CheckstyleGrader("sun_checks.xml", 1.0, 20.0);
        List<Result> results = grader.grade(List.of(badFormattingTarget, missingCommentsTarget));
        // There are 11 violations (because package-info violation counted only once).
        TestUtilities.assertResultsMatch(results, 1, 9.0, 20.0);
    }
}
