package com.spertus.jacquard;

import com.github.javaparser.ast.Modifier;
import com.spertus.jacquard.common.*;
import com.spertus.jacquard.syntaxgrader.FieldModifierChecker;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FieldModifierTest {
    Autograder autograder = new Autograder();

    @Test
    public void testWithPenalizeMissingFields() throws URISyntaxException {
        FieldModifierChecker checker = FieldModifierChecker.makeChecker("Private/final check", 1.0,
                List.of("behavior", "maxHearts", "maxDamage", "minDamage", "type", "nosuchfield"),
                List.of(Modifier.finalModifier(), Modifier.privateModifier()),
                List.of(),
                true);
        Target target = TestUtilities.getTargetFromResource("good/Mob.java");
        List<Result> results = autograder.grade(checker, target);
        assertEquals(6, results.size());
        assertEquals(4.0, TestUtilities.getTotalScore(results));
    }

    @Test
    public void testWithoutPenalizeMissingFields() throws URISyntaxException {
        FieldModifierChecker checker = FieldModifierChecker.makeChecker("Private/final check", 1.0,
                List.of("behavior", "maxHearts", "maxDamage", "minDamage", "type", "nosuchfield"),
                List.of(Modifier.finalModifier(), Modifier.privateModifier()),
                List.of(),
                false);
        Target target = TestUtilities.getTargetFromResource("good/Mob.java");
        List<Result> results = autograder.grade(checker, target);
        assertEquals(5, results.size());
        assertEquals(4.0, TestUtilities.getTotalScore(results));
    }
}
