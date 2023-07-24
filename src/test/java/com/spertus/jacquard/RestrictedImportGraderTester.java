package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.syntaxgrader.*;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RestrictedImportGraderTester {
    @BeforeAll
    public static void setup() {
        Autograder.initForTest();
    }

    @Test
    public void testNoneForbidden() throws URISyntaxException {
        RestrictedImportGrader checker = new RestrictedImportGrader(
                1.0, List.of("java.util", "java.lang"));
        List<Result> results = checker.grade(TestUtilities.getTargetFromResource("good/Import.java"));
        TestUtilities.assertResultsMatch(results, 1, 1.0, 1.0);
    }

    @Test
    public void testOneForbidden() throws URISyntaxException {
        RestrictedImportGrader checker = new RestrictedImportGrader(
                1.0, List.of("java.util", "java.lang"));
        List<Result> results = checker.grade(TestUtilities.getTargetFromResource("good/ImportWildcards.java"));
        TestUtilities.assertResultsMatch(results, 1, 0.0, 1.0);
        String message = results.get(0).getMessage();
        assertTrue(message.contains("javax.ejb"));  // not whitelisted
        assertFalse(message.contains("java.util")); // whitelisted
    }
}
