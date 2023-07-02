package com.spertus.jacquard;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.spertus.jacquard.common.*;
import com.spertus.jacquard.syntaxgrader.SyntaxConditionGrader;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SyntaxConditionGraderTest {
    SyntaxConditionGrader overrideGrader;

    @BeforeEach
    public void setup() {
        overrideGrader = new SyntaxConditionGrader(
                "toString() override test",
                "toString() method with override annotation",
                5.0,
                node -> {
                    if (node instanceof MethodDeclaration methodDecl) {
                        return methodDecl.getAnnotationByClass(Override.class).isPresent()
                                && methodDecl.getNameAsString().equals("toString");
                    }
                    return false;
                });
    }

    @Test
    public void testHasToStringOverrideWhenPresent() throws URISyntaxException {
        List<Result> results = overrideGrader.grade(TestUtilities.getTargetFromResource("good/Mob.java"));
        assertEquals(1, results.size());
        assertEquals(5.0, results.get(0).score());
        assertEquals(5.0, results.get(0).maxScore());
    }

    @Test
    public void testHasToStringOverrideWhenAbsent() throws URISyntaxException {
        List<Result> results = overrideGrader.grade(TestUtilities.getTargetFromResource("good/PrimeChecker.java"));
        assertEquals(1, results.size());
        assertEquals(0, results.get(0).score());
        assertEquals(5.0, results.get(0).maxScore());
    }

    @Test
    public void testHasToStringOverrideWhenAnnotationMissing() throws URISyntaxException {
        List<Result> results = overrideGrader.grade(TestUtilities.getTargetFromResource("good/NoOverrideAnnotation.java"));
        assertEquals(1, results.size());
        assertEquals(0, results.get(0).score());
        assertEquals(5.0, results.get(0).maxScore());
    }
}
