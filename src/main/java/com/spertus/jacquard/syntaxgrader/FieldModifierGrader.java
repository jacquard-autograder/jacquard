package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.google.common.base.Preconditions;
import com.spertus.jacquard.common.Result;

import java.util.*;

/**
 * Checks whether the correct modifiers are used for fields (instance and
 * static variables/constants). For example, this could be used to verify
 * that certain instance variables are declared {@code private} or {@code final}.
 */
public final class FieldModifierGrader extends ModifierGrader {
    private static final String DEFAULT_GRADER_NAME = "field modifier grader";

    private FieldModifierGrader(
            final String name,
            final double maxScorePerInstance,
            final List<String> fieldNames,
            final List<Modifier> requiredModifiers,
            final List<Modifier> optionalModifiers,
            final boolean penalizeMissingFields) {
        super(name, maxScorePerInstance, fieldNames, requiredModifiers, optionalModifiers, penalizeMissingFields);
        adapter = new Adapter();
    }

    /**
     * Creates a field modifier grader. If {@code penalizeMissingFields} is
     * true and a field is not found, a {@link Result} will be created with a
     * score of 0 and a maximum score of {@code maxScorePerInstance}. Otherwise,
     * no {@code Result} will be created for missing fields.
     *
     * @param name                  the name
     * @param maxScorePerInstance   the maximum score for each variable
     * @param varNames              the names of the variables to check
     * @param requiredModifiers     modifiers that must be used on each variable
     * @param optionalModifiers     modifiers that may be used on variables
     * @param penalizeMissingFields whether to apply a penalty to missing fields
     * @return a new instance
     */
    public static FieldModifierGrader makeChecker(
            final String name,
            final double maxScorePerInstance,
            final List<String> varNames,
            final List<Modifier> requiredModifiers,
            final List<Modifier> optionalModifiers,
            boolean penalizeMissingFields
    ) {
        return new FieldModifierGrader(
                name,
                maxScorePerInstance,
                varNames,
                requiredModifiers,
                optionalModifiers,
                penalizeMissingFields
        );
    }

    /**
     * Creates a field modifier grader with a default name. If
     * {@code penalizeMissingFields} is true and a field is not found, a
     * {@link Result} will be created with a score of 0 and a maximum score of
     * {@code maxScorePerInstance}. Otherwise, no {@code Result} will be created
     * for missing fields.
     *
     * @param maxScorePerInstance   the maximum score for each variable
     * @param varNames              the names of the variables to check
     * @param requiredModifiers     modifiers that must be used on each variable
     * @param optionalModifiers     modifiers that may be used on variables
     * @param penalizeMissingFields whether to apply a penalty to missing fields
     * @return a new instance
     */
    public static FieldModifierGrader makeChecker(
            final double maxScorePerInstance,
            final List<String> varNames,
            final List<Modifier> requiredModifiers,
            final List<Modifier> optionalModifiers,
            final boolean penalizeMissingFields
    ) {
        return makeChecker(
                DEFAULT_GRADER_NAME,
                maxScorePerInstance,
                varNames, requiredModifiers, optionalModifiers,
                penalizeMissingFields
        );
    }

    private class Adapter extends ModifierGrader.Adapter { // NOPMD
        @Override
        public void visit(final VariableDeclarator vd, final List<Result> collector) {
            if (isField(vd)) {
                final FieldDeclaration fd = getFieldDeclaration(vd);
                process(collector, fd, vd.getNameAsString(), fd.getModifiers());
            }

            super.visit(vd, collector);
        }
    }

    private boolean isField(final VariableDeclarator vd) {
        return vd.getParentNode().isPresent()
                && vd.getParentNode().get() instanceof FieldDeclaration;
    }

    // This should be called only if isField() is true.
    private FieldDeclaration getFieldDeclaration(final VariableDeclarator vd) {
        Preconditions.checkState(isField(vd));
        // The precondition guarantees the safety of the get() and cast.
        return (FieldDeclaration) vd.getParentNode().get();
    }
}
