package com.spertus.jacquard;

import com.github.javaparser.ast.Modifier;
import com.spertus.jacquard.common.*;
import com.spertus.jacquard.syntaxgrader.FieldModifierGrader;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FieldModifierTest {
    @BeforeAll()
    public static void init() {
        Autograder.initForTest();
    }

    @Test
    public void testWithPenalizeMissingFields() throws URISyntaxException {
        FieldModifierGrader checker = FieldModifierGrader.makeChecker("Private/final check", 1.0,
                List.of("behavior", "maxHearts", "maxDamage", "minDamage", "type", "nosuchfield"),
                List.of(Modifier.finalModifier(), Modifier.privateModifier()),
                List.of(),
                true);
        Target target = TestUtilities.getTargetFromResource("good/Mob.java");
        List<Result> results = checker.grade(target);
        assertEquals(6, results.size());
        assertEquals(4.0, TestUtilities.getTotalScore(results));
    }

    @Test
    public void testWithoutPenalizeMissingFields() throws URISyntaxException {
        FieldModifierGrader checker = FieldModifierGrader.makeChecker("Private/final check", 1.0,
                List.of("behavior", "maxHearts", "maxDamage", "minDamage", "type", "nosuchfield"),
                List.of(Modifier.finalModifier(), Modifier.privateModifier()),
                List.of(),
                false);
        Target target = TestUtilities.getTargetFromResource("good/Mob.java");
        List<Result> results = checker.grade(target);
        assertEquals(5, results.size());
        assertEquals(4.0, TestUtilities.getTotalScore(results));
    }
}
