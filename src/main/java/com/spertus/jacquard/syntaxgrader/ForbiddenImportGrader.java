package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.spertus.jacquard.common.Result;

import java.util.*;

/**
 * Checks whether forbidden imports appear in a submission. This is
 * one of several graders checking imports.
 *
 * @see RestrictedImportGrader
 * @see RequiredImportGrader
 */
public class ForbiddenImportGrader extends SyntaxCheckGrader {
    private static final String GRADER_NAME = "forbidden import grader";

    /**
     * Creates an import grader that tests whether any forbidden packages
     * are imported.
     *
     * @param name              the name of this checker
     * @param maxPoints         the number of points to award if none are included
     * @param forbiddenPackages forbidden package names
     */
    public ForbiddenImportGrader(
            final String name,
            final double maxPoints,
            final List<String> forbiddenPackages) {
        super(name, maxPoints, null);
        adapter = new ImportCheckerAdapter(forbiddenPackages);
    }

    /**
     * Creates an import checker with the default name that tests whether any
     * forbidden packages are imported.
     *
     * @param maxPoints         the number of points to award if none are included
     * @param forbiddenPackages forbidden package names
     */
    public ForbiddenImportGrader(
            final double maxPoints,
            final List<String> forbiddenPackages) {
        this(GRADER_NAME, maxPoints, forbiddenPackages);
    }

    @Override
    public double getTotalMaxScore() {
        return maxScorePerInstance;
    }

    @Override
    public void finalizeResults(final List<Result> results) {
        ((ImportCheckerAdapter) adapter).finalizeResults(results);
    }

    private class ImportCheckerAdapter extends VoidVisitorAdapter<List<Result>> { // NOPMD
        private final List<String> forbiddenPackages;
        private final Set<String> foundForbiddenPackages = new HashSet<>();

        private ImportCheckerAdapter(final List<String> forbiddenPackages) {
            this.forbiddenPackages = forbiddenPackages;
        }

        @Override
        public void visit(final ImportDeclaration importDecl, final List<Result> results) {
            String importName = importDecl.getNameAsString();
            for (String pkg : forbiddenPackages) {
                if (importName.startsWith(pkg)) {
                    foundForbiddenPackages.add(pkg);
                    break;
                }
            }
            super.visit(importDecl, results);
        }

        public void finalizeResults(List<Result> results) {
            if (foundForbiddenPackages.isEmpty()) {
                results.add(makeSuccessResult("No forbidden packages imported."));
            } else {
                results.add(makeFailingResult("Forbidden packages imported: " +
                        String.join(", ", foundForbiddenPackages)));
            }
        }
    }
}
