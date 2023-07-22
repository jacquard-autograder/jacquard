package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.spertus.jacquard.common.Result;

import java.util.*;

/**
 * Checks whether the correct modifiers are used for methods (instance and
 * static). For example, this could be used to verify that certain methods
 * are declared {@code public} or {@code static}.
 */
public final class MethodModifierChecker extends ModifierChecker {
    private static final String DEFAULT_GRADER_NAME = "method modifier checker";

    private MethodModifierChecker(String name, double maxScorePerInstance, List<String> methodNames, List<Modifier> requiredModifiers, List<Modifier> optionalModifiers,
                                  boolean penalizeMissingMethods) {
        super(name, maxScorePerInstance, methodNames, requiredModifiers, optionalModifiers, penalizeMissingMethods);
        adapter = new Adapter();
    }

    /**
     * Creates a method modifier checker. If {@code penalizeMissingMethods} is
     * true and a method is not found, a {@link Result} will be created with a
     * score of 0 and a maximum score of {@code maxScorePerInstance}. Otherwise,
     * no {@code Result} will be created for missing methods.
     *
     * @param name                   the name, which is used in the {@link Result}
     * @param maxScorePerInstance    the maximum score for each method
     * @param varNames               the names of the methods to check
     * @param requiredModifiers      modifiers that must be used on each method
     * @param optionalModifiers      modifiers that may be used on methods
     * @param penalizeMissingMethods whether to apply a penalty to missing methods
     * @return a new instance
     */
    public static MethodModifierChecker makeChecker(
            String name,
            double maxScorePerInstance,
            List<String> varNames,
            List<Modifier> requiredModifiers,
            List<Modifier> optionalModifiers,
            boolean penalizeMissingMethods
    ) {
        return new MethodModifierChecker(
                name,
                maxScorePerInstance,
                varNames,
                requiredModifiers,
                optionalModifiers,
                penalizeMissingMethods
        );
    }

    /**
     * Creates a method modifier checker with a default name. If
     * {@code penalizeMissingMethods} is true and a method is not found, a
     * {@link Result} will be created with a score of 0 and a maximum score of
     * {@code maxScorePerInstance}. Otherwise, no {@code Result} will be created
     * for missing methods.
     *
     * @param maxScorePerInstance    the maximum score for each method
     * @param varNames               the names of the methods to check
     * @param requiredModifiers      modifiers that must be used on each method
     * @param optionalModifiers      modifiers that may be used on methods
     * @param penalizeMissingMethods whether to apply a penalty to missing methods
     * @return a new instance
     */
    public static MethodModifierChecker makeChecker(
            double maxScorePerInstance,
            List<String> varNames,
            List<Modifier> requiredModifiers,
            List<Modifier> optionalModifiers,
            boolean penalizeMissingMethods
    ) {
        return makeChecker(
                DEFAULT_GRADER_NAME,
                maxScorePerInstance,
                varNames, requiredModifiers, optionalModifiers,
                penalizeMissingMethods
        );
    }


    private class Adapter extends ModifierChecker.Adapter { // NOPMD
        @Override
        public void visit(MethodDeclaration md, List<Result> collector) {
            process(collector, md, md.getNameAsString(), md.getModifiers());
            super.visit(md, collector);
        }
    }
}
