package com.spertus.jacquard;

import com.github.javaparser.ast.Modifier;
import com.spertus.jacquard.common.*;
import com.spertus.jacquard.syntaxgrader.FieldModifierChecker;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FieldModifierTest {
    @Test
    public void testFieldModiferChecker() throws URISyntaxException {
        FieldModifierChecker checker = new FieldModifierChecker("Private/final check", 1.0,
                List.of("behavior", "maxHearts", "maxDamage", "minDamage", "type"),
                List.of(Modifier.finalModifier(), Modifier.privateModifier()),
                List.of());
        Target target = TestUtilities.getTargetFromResource("good/Mob.java");
        List<Result> results = checker.grade(target);
        assertEquals(5, results.size());
        assertEquals(4.0, TestUtilities.getTotalScore(results));
    }
}
