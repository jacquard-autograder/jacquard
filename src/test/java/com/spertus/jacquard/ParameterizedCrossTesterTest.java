package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.crosstester.*;
import com.spertus.jacquard.exceptions.ClientException;
import com.spertus.jacquard.parameterizedcrosstester.GeneralizedAdderTest;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParameterizedCrossTesterTest {
    @BeforeAll()
    public static void init() {
        Autograder.initForTest();
    }

    @Test
    public void testClassesWithoutIntParameters() throws ClientException {
        ParameterizedCrossTester grader = new ParameterizedCrossTester(GeneralizedAdderTest.class,
                "crosstester1.csv");
        List<Result> results = grader.run();
        assertEquals(2, results.size());
        testResult(results.get(0), 2, 2);
        testResult(results.get(1), 1, 1);
        assertEquals(3, TestUtilities.getTotalScore(results));
    }

    private void testResult(Result result, double score, double maxScore) {
        assertEquals(score, result.getScore());
        assertEquals(maxScore, result.getMaxScore());
    }

    @Test
    public void testClassesWithIntParameters() throws ClientException {
        ParameterizedCrossTester grader = new ParameterizedCrossTester(GeneralizedAdderTest.class,
               "crosstester2.csv");
        List<Result> results = grader.run();
        assertEquals(4, results.size());
        // Tests 0, 1, and 2 will fail. Test 3 will succeed. This is as intended.
        testResult(results.get(0), 1, 1);
        testResult(results.get(1), 2, 2);
        testResult(results.get(2), 3, 3);
        testResult(results.get(3), 4, 4);
    }

}
