package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.spertus.jacquard.common.Result;

import java.util.*;

/**
 * Superclass for classes checking modifiers of syntactic elements.
 */
abstract class ModifierChecker extends SyntaxChecker {
    private final List<String> itemNames;
    private final boolean penalizeMissing;
    private final Set<String> missingVars;
    private final List<Modifier> requiredModifiers;
    private final List<Modifier> optionalModifiers;

    /**
     * Creates a modifier checker. If {@code penalizeMissingItems} is
     * true and an item is not found, a {@link Result} will be created with a
     * score of 0 and a maximum score of {@code maxScorePerInstance}. Otherwise,
     * no {@code Result} will be created for missing items.
     *
     * @param name                 the name
     * @param maxScorePerInstance  the maximum score for each item
     * @param itemNames            the names of the items to check
     * @param requiredModifiers    modifiers that must be used on each item
     * @param optionalModifiers    modifiers that may be used on items
     * @param penalizeMissingItems whether to apply a penalty to missing items
     */
    protected ModifierChecker(
            String name,
            double maxScorePerInstance,
            List<String> itemNames,
            List<Modifier> requiredModifiers,
            List<Modifier> optionalModifiers,
            boolean penalizeMissingItems) {
        super(name, maxScorePerInstance, null);
        this.itemNames = itemNames;
        this.requiredModifiers = requiredModifiers;
        this.optionalModifiers = optionalModifiers;
        this.penalizeMissing = penalizeMissingItems;
        missingVars = new HashSet<>(itemNames);
        // concrete subclass sets adapter
    }

    @Override
    public void finalizeResults(List<Result> results) {
        if (!penalizeMissing) {
            return;
        }
        for (String var : missingVars) {
            results.add(makeFailingResult("Did not find expected variable " + var));
        }
    }

    @Override
    public double getTotalMaxScore() {
        return maxScorePerInstance * itemNames.size();
    }

    protected class Adapter extends VoidVisitorAdapter<List<Result>> {

        protected String getEnclosingClassName(Node node) {
            if (node.getParentNode().isPresent() &&
                    node.getParentNode().get() instanceof ClassOrInterfaceDeclaration classOrInterface) {
                return classOrInterface.getNameAsString();
            } else {
                return "CLASS UNKNOWN";
            }
        }

        protected void process(List<Result> collector, Node node, String name, List<Modifier> modifiers) {
            if (!missingVars.contains(name)) {
                return;
            }

            // Mark that this field has been found.
            missingVars.remove(name);

            // Make a copy of this instance variable's modifiers.
            final List<Modifier> mods = new ArrayList<>(modifiers);

            // Ensure that all required modifiers are present, removing them.
            for (final Modifier modifier : requiredModifiers) {
                if (mods.contains(modifier)) {
                    mods.remove(modifier);
                } else {
                    collector.add(
                            makeFailingResult(
                                    String.format(
                                            "%s is missing required modifier '%s'.",
                                            name,
                                            modifier.toString().trim())));
                    return;
                }
            }

            // Ensure that any remaining modifiers are permitted.
            for (final Modifier modifier : mods) {
                if (!optionalModifiers.contains(modifier)) {
                    collector.add(
                            makeFailingResult(
                                    String.format(
                                            "%s has forbidden modifier '%s'.",
                                            name,
                                            modifier.toString().trim())));
                    return;
                }
            }
            collector.add(makeSuccessResult(
                    String.format(
                            "%s.%s is declared correctly.",
                            getEnclosingClassName(node),
                            name)));
        }
    }
}
