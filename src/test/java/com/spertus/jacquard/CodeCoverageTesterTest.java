package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.coverage.*;
import com.spertus.jacquard.coveragetests.PrimeCheckerTest;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeCoverageGraderTest {
    @Test
    public void testLinearScorer() throws URISyntaxException {
        Scorer scorer = new LinearScorer(.5, 10);
        CodeCoverageGrader grader = new CodeCoverageGrader(scorer);
        List<Result> results = grader.grade(
                TestUtilities.getTargetFromResource("com/spertus/jacquard/coveragetests/PrimeChecker.java"));
        assertEquals(1, results.size());
        assertEquals(7.75, results.get(0).getScore());
        assertEquals(10.0, results.get(0).getMaxScore());
    }

    @Test
    public void testJacoco() throws Exception {
        CodeCoverageGrader.runJacoco(
                "com.spertus.jacquard.coveragetests.PrimeChecker",
                "build/classes/java/test/com/spertus/jacquard/coverage/PrimeChecker.class",
                PrimeCheckerTest.class
            );
    }

    @Test
    public void testCoreTutorial3() throws Exception {
        CoreTutorial3 ctp = new CoreTutorial3(System.out);
        ctp.execute(com.spertus.jacquard.coveragetests.PrimeChecker.class,
                com.spertus.jacquard.coveragetests.PrimeCheckerTest.class);
    }
}
