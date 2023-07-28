package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.syntaxgrader.*;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

public class ImportBlocklistedGraderTester {
    static Target importTarget;

    @BeforeAll
    public static void setup() throws URISyntaxException {
        Autograder.initForTest();
        importTarget = TestUtilities.getTargetFromResource("good/Import.java");
    }

    @Test
    public void testRepeatability() {
        ImportBlocklistedGrader grader = new ImportBlocklistedGrader(
                1.0, List.of("java.util", "javax"));
        TestUtilities.testRepeatability(grader, importTarget);
    }

    @Test
    public void testNoForbiddenImports() throws URISyntaxException {
        ImportBlocklistedGrader grader = new ImportBlocklistedGrader(
                1.0, List.of("javax"));
        List<Result> results = grader.grade(importTarget);
        TestUtilities.assertResultsMatch(results, 1, 1.0, 1.0);
    }

    @Test
    public void testSomeForbiddenImports() throws URISyntaxException {
        ImportBlocklistedGrader grader = new ImportBlocklistedGrader(
                1.0, List.of("java.util", "javax"));
        List<Result> results = grader.grade(importTarget);
        TestUtilities.assertResultsMatch(results, 1, 0.0, 1.0);
    }

    @Test
    public void testAllForbiddenImports() throws URISyntaxException {
        ImportBlocklistedGrader grader = new ImportBlocklistedGrader(
                1.0, List.of("java.util", "javax"));
        List<Result> results = grader.grade(importTarget);
        TestUtilities.assertResultsMatch(results, 1, 0.0, 1.0);
    }
}
