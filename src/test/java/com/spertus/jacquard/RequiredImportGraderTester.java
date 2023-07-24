package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.syntaxgrader.RequiredImportGrader;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

public class RequiredImportGraderTester {
    @BeforeAll
    public static void setup() {
        Autograder.initForTest();
    }

    @Test
    public void fullMatch() throws URISyntaxException {
        RequiredImportGrader checker = new RequiredImportGrader(
                1.0, List.of("java.util.Random"));
        List<Result> results = checker.grade(TestUtilities.getTargetFromResource("good/Import.java"));
        TestUtilities.assertResultsMatch(results, 1, 1.0, 1.0);
    }

    @Test
    public void halfMatch() throws URISyntaxException {
        RequiredImportGrader checker = new RequiredImportGrader(
                1.0, List.of("java.util.Random", "java.util.List"));
        List<Result> results = checker.grade(TestUtilities.getTargetFromResource("good/Import.java"));
        TestUtilities.assertResultsMatch(results, 2, 1.0, 2.0);
    }

    @Test
    public void noMatch() throws URISyntaxException {
        RequiredImportGrader checker = new RequiredImportGrader(
                1.0, List.of("java.util.*", "java.util.List"));
        List<Result> results = checker.grade(TestUtilities.getTargetFromResource("good/ForStatements.java"));
        TestUtilities.assertResultsMatch(results, 2, 0, 2.0);
    }

    @Test
    public void bothWildcards() throws URISyntaxException {
        RequiredImportGrader checker = new RequiredImportGrader(
                1.0, List.of("java.util.*", "java.lang.*"));
        List<Result> results = checker.grade(TestUtilities.getTargetFromResource("good/ImportWildcards.java"));
        TestUtilities.assertResultsMatch(results, 2, 1.0, 2.0);
    }

    @Test
    public void sourceWildcard() throws URISyntaxException {
        RequiredImportGrader checker = new RequiredImportGrader(
                1.0, List.of("java.util.List", "java.util.ArrayList"));
        List<Result> results = checker.grade(TestUtilities.getTargetFromResource("good/ImportWildcards.java"));
        TestUtilities.assertResultsMatch(results, 2, 2.0, 2.0);
    }

    @Test
    public void importWildcard() throws URISyntaxException {
        RequiredImportGrader checker = new RequiredImportGrader(
                1.0, List.of("java.util.*"));
        List<Result> results = checker.grade(TestUtilities.getTargetFromResource("good/Import.java"));
        TestUtilities.assertResultsMatch(results, 1, 0.0, 1.0);
    }
}
