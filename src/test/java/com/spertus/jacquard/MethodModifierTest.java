package com.spertus.jacquard;

import com.github.javaparser.ast.Modifier;
import com.spertus.jacquard.common.*;
import com.spertus.jacquard.syntaxgrader.*;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodModifierTest {
    @BeforeAll()
    public static void init() {
        Autograder.initForTest();
    }

    @Test
    public void testWithPenalizeMissingMethods() throws URISyntaxException {
       MethodModifierChecker checker = MethodModifierChecker.makeChecker(
               "Public method check", 1.0,
                List.of("getMinDamage", "getNumHearts", "methodDoesNotExist"),
                List.of(Modifier.publicModifier()),
                List.of(Modifier.finalModifier()),
               true);
        Target target = TestUtilities.getTargetFromResource("good/Mob.java");
        List<Result> results = checker.grade(target);
        assertEquals(3, results.size());
        assertEquals(1.0, TestUtilities.getTotalScore(results));
    }

    @Test
    public void testWithoutPenalizeMissingMethods() throws URISyntaxException {
        MethodModifierChecker checker = MethodModifierChecker.makeChecker(
                "Public method check", 1.0,
                List.of("getMinDamage", "getNumHearts", "methodDoesNotExist"),
                List.of(Modifier.publicModifier()),
                List.of(Modifier.finalModifier()),
                false);
        Target target = TestUtilities.getTargetFromResource("good/Mob.java");
        List<Result> results = checker.grade(target);
        assertEquals(2, results.size());
        assertEquals(1.0, TestUtilities.getTotalScore(results));
    }
}
