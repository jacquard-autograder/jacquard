package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.syntaxgrader.ImportRequiredGrader;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

public class ImportRequiredGraderTester {
    @BeforeAll
    public static void setup() {
        Autograder.initForTest();
    }

    @Test
    public void testRepeatability() throws URISyntaxException {
        ImportRequiredGrader grader = new ImportRequiredGrader(
                1.0, List.of("java.util.Random", "java.util.List"));
        TestUtilities.testRepeatability(grader, "good/Import.java");
    }

    @Test
    public void fullMatch() throws URISyntaxException {
        ImportRequiredGrader grader = new ImportRequiredGrader(
                1.0, List.of("java.util.Random"));
        List<Result> results = grader.grade(TestUtilities.getTargetFromResource("good/Import.java"));
        TestUtilities.assertResultsMatch(results, 1, 1.0, 1.0);
    }

    @Test
    public void halfMatch() throws URISyntaxException {
        ImportRequiredGrader grader = new ImportRequiredGrader(
                1.0, List.of("java.util.Random", "java.util.List"));
        List<Result> results = grader.grade(TestUtilities.getTargetFromResource("good/Import.java"));
        TestUtilities.assertResultsMatch(results, 2, 1.0, 2.0);
    }

    @Test
    public void noMatch() throws URISyntaxException {
        ImportRequiredGrader grader = new ImportRequiredGrader(
                1.0, List.of("java.util.*", "java.util.List"));
        List<Result> results = grader.grade(TestUtilities.getTargetFromResource("good/ForStatements.java"));
        TestUtilities.assertResultsMatch(results, 2, 0, 2.0);
    }

    @Test
    public void bothWildcards() throws URISyntaxException {
        ImportRequiredGrader grader = new ImportRequiredGrader(
                1.0, List.of("java.util.*", "java.lang.*"));
        List<Result> results = grader.grade(TestUtilities.getTargetFromResource("good/ImportWildcards.java"));
        TestUtilities.assertResultsMatch(results, 2, 1.0, 2.0);
    }

    @Test
    public void sourceWildcard() throws URISyntaxException {
        ImportRequiredGrader grader = new ImportRequiredGrader(
                1.0, List.of("java.util.List", "java.util.ArrayList"));
        List<Result> results = grader.grade(TestUtilities.getTargetFromResource("good/ImportWildcards.java"));
        TestUtilities.assertResultsMatch(results, 2, 2.0, 2.0);
    }

    @Test
    public void importWildcard() throws URISyntaxException {
        ImportRequiredGrader grader = new ImportRequiredGrader(
                1.0, List.of("java.util.*"));
        List<Result> results = grader.grade(TestUtilities.getTargetFromResource("good/Import.java"));
        TestUtilities.assertResultsMatch(results, 1, 0.0, 1.0);
    }
}
