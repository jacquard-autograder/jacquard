package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.coverage.*;
import com.spertus.jacquard.coveragetests.*;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeCoverageTesterTest {
    @BeforeAll()
    public static void init() {
        Autograder.initForTest();
    }

    @Test
    public void testLinearScorer() throws URISyntaxException {
        Scorer scorer = new LinearScorer(.5, 10);
        CodeCoverageTester tester = new CodeCoverageTester(scorer, PrimeChecker.class, PrimeCheckerTest.class);
        List<Result> results = tester.run();
        assertEquals(1, results.size());
        assertEquals(7.75, results.get(0).getScore());
        assertEquals(10.0, results.get(0).getMaxScore());
    }
}
