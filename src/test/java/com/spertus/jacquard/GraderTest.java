package com.spertus.jacquard;

import com.github.javaparser.ast.Modifier;
import com.spertus.jacquard.common.*;
import com.spertus.jacquard.syntaxgrader.*;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraderTest {
    @BeforeAll
    public static void setup() {
        Autograder.initForTest();
    }

    @Test
    public void testTwoGraders() throws URISyntaxException {
        FieldModifierGrader grader1 = FieldModifierGrader.makeChecker("Private/final check", 1.0,
                List.of("behavior", "maxHearts", "maxDamage", "minDamage", "type", "nosuchfield"),
                List.of(Modifier.finalModifier(), Modifier.privateModifier()),
                List.of(),
                true);
        MethodModifierGrader grader2 = new MethodModifierGrader(
                1.0,
                List.of("getMinDamage", "getNumHearts", "methodDoesNotExist"),
                List.of(Modifier.publicModifier()),
                List.of(Modifier.finalModifier()),
                true);
        Target target = TestUtilities.getTargetFromResource("good/Mob.java");

        List<Result> results1 = grader1.grade(target);
        List<Result> results2 = grader2.grade(target);
        results1.addAll(results2);
        List<Result> resultsAll = Grader.gradeAll(target, grader1, grader2);
        assertEquals(results1, resultsAll);
    }
}
