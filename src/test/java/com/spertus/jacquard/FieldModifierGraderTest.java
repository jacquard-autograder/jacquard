package com.spertus.jacquard;

import com.github.javaparser.ast.Modifier;
import com.spertus.jacquard.common.*;
import com.spertus.jacquard.syntaxgrader.FieldModifierGrader;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

public class FieldModifierGraderTest {
    static Target mobTarget;

    @BeforeAll()
    public static void init() throws URISyntaxException {
        Autograder.initForTest();
        mobTarget = TestUtilities.getTargetFromResource("good/Mob.java");
    }

    @Test
    public void testRepeatability() {
        FieldModifierGrader grader = FieldModifierGrader.makeChecker("Private/final check", 1.0,
                List.of("behavior", "maxHearts", "maxDamage", "minDamage", "type", "nosuchfield"),
                List.of(Modifier.finalModifier(), Modifier.privateModifier()),
                List.of(),
                true);
        TestUtilities.testTwice(grader, mobTarget);
    }

    @Test
    public void testWithPenalizeMissingFields() throws URISyntaxException {
        FieldModifierGrader grader = FieldModifierGrader.makeChecker("Private/final check", 1.0,
                List.of("behavior", "maxHearts", "maxDamage", "minDamage", "type", "nosuchfield"),
                List.of(Modifier.finalModifier(), Modifier.privateModifier()),
                List.of(),
                true);
        List<Result> results = grader.grade(mobTarget);
        TestUtilities.assertResultsMatch(results, 6, 4.0, 6.0);
    }

    @Test
    public void testWithoutPenalizeMissingFields() throws URISyntaxException {
        FieldModifierGrader grader = FieldModifierGrader.makeChecker("Private/final check", 1.0,
                List.of("behavior", "maxHearts", "maxDamage", "minDamage", "type", "nosuchfield"),
                List.of(Modifier.finalModifier(), Modifier.privateModifier()),
                List.of(),
                false);
        Target target = TestUtilities.getTargetFromResource("good/Mob.java");
        List<Result> results = grader.grade(target);
        TestUtilities.assertResultsMatch(results, 5, 4.0, 5.0);
    }
}
