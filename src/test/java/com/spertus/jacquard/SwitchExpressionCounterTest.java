package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.exceptions.ClientException;
import com.spertus.jacquard.syntaxgrader.SwitchExpressionCounter;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SwitchExpressionCounterTest {
    private static final double MAX_SCORE = 10.0;

    private SwitchExpressionCounter counter;

    @BeforeAll()
    public static void init() {
        Autograder.initForTest();
    }

    @BeforeEach
    public void setup() throws ClientException {
        counter = new SwitchExpressionCounter(MAX_SCORE, 1, 2);
    }

    @Test
    public void scoreIsMaxIfRightNumber() throws URISyntaxException {
        Target target = TestUtilities.getTargetFromResource("good/Mob.java");
        List<Result> results = counter.grade(target);
        assertEquals(1, results.size());
        assertEquals(MAX_SCORE, results.get(0).getScore());
    }
}
