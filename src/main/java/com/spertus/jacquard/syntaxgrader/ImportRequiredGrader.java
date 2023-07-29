package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.spertus.jacquard.common.Result;
import com.spertus.jacquard.exceptions.ClientException;

import java.util.*;

/**
 * Checks whether required imports appear in a submission. This is
 * one of several graders checking imports.
 *
 * @see ImportBlocklistedGrader
 * @see ImportDisallowedGrader
 */
public class ImportRequiredGrader extends SyntaxCheckGrader {
    private static final String GRADER_NAME = "required import checker";

    /**
     * Creates an import checker that awards points per required import that is
     * found. Required imports may end with asterisks but must not
     * end with semicolons. For example, legal values include "java.util.*"
     * and "java.io.IOException".
     *
     * @param name            the name of this checker
     * @param pointsPerImport the number of points per required import
     * @param requiredImports required imports
     * @throws ClientException if requiredImports is empty or has malformed entries
     */
    public ImportRequiredGrader(
            final String name,
            final double pointsPerImport,
            final List<String> requiredImports) {
        super(name, pointsPerImport, null);
        requiredImports.forEach(
                s -> {
                    if (s.endsWith(";") || s.startsWith("import ")) {
                        throw new ClientException("Required import " + s + " is malformed.");
                    }
                });
        adapter = new ImportCheckerAdapter(requiredImports);
    }

    /**
     * Creates an import checker with the default name that awards points per
     * required import that is found. Required imports may end with asterisks
     * but must not end with semicolons. For example, legal values include
     * "java.util.*" and "java.io.IOException".
     *
     * @param pointsPerImport the number of points per required import
     * @param requiredImports required imports
     * @throws ClientException if requiredImports is empty or has malformed entries
     */
    public ImportRequiredGrader(
            final double pointsPerImport,
            final List<String> requiredImports) {
        this(GRADER_NAME, pointsPerImport, requiredImports);
    }

    @Override
    public void initialize() {
        ((ImportCheckerAdapter) adapter).initialize();
    }

    private static boolean importMatches(final ImportDeclaration importDecl, final String requirement) {
        final String importName = importDecl.getNameAsString();
        if (importDecl.isAsterisk()) {
            // both are wildcards
            if (requirement.equals(importName + ".*")) {
                return true;
            }
            // importDecl is wildcard but requirement is not:
            // If the importName (e.g., "java.util") is a prefix of the requirement
            // (e.g., "java.util.*" or "java.util.List"), it's a match unless the
            // requirement has more than one additional period (e.g.,
            // "java.util.concurrent.timeunit").
            return requirement.startsWith(importName) &&
                    requirement.length() > importName.length() &&
                    !requirement.substring(importName.length() + 1).contains(".");
        } else {
            if (requirement.endsWith(".*")) {
                // importDecl is not wildcard but requirement is
                return false;
            } else {
                // neither is a wildcard
                return importName.equals(requirement);
            }
        }
    }

    @Override
    public void finalizeResults(final List<Result> results) {
        ((ImportCheckerAdapter) adapter).finalizeResults(results);
    }

    private class ImportCheckerAdapter extends VoidVisitorAdapter<List<Result>> { // NOPMD
        private final List<String> requiredImports;
        // Use separate data structure to prevent ConcurrentModificationException.
        private final Set<String> matchedImports = new HashSet<>();

        private ImportCheckerAdapter(final List<String> requiredImports) {
            super();
            this.requiredImports = requiredImports;
        }

        private void initialize() { // NOPMD (false positive)
            matchedImports.clear();
        }

        @Override
        public void visit(final ImportDeclaration importDecl, final List<Result> results) {
            for (final String requirement : requiredImports) {
                if (matchedImports.contains(requirement)) {
                    continue;
                }
                if (importMatches(importDecl, requirement)) {
                    results.add(makeSuccessResult(
                            maxScorePerInstance,
                            "Found import " + requirement));
                    matchedImports.add(requirement);
                    // An importDecl containing an asterisk can match multiple requirements.
                    if (!importDecl.isAsterisk()) {
                        break;
                    }
                }
            }
            super.visit(importDecl, results);
        }

        private void finalizeResults(List<Result> results) { // NOPMD (false positive)
            for (final String requirement : requiredImports) {
                if (!matchedImports.contains(requirement)) {
                    results.add(makeFailingResult("Expected import " + requirement + " not found."));
                }
            }
        }
    }
}
