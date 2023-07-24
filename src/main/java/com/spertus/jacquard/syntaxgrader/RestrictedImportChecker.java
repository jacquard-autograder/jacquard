package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.spertus.jacquard.common.Result;

import java.util.*;

/**
 * Checks whether all imports are within a list of permitted packages.
 * This is one of several graders checking imports.
 *
 * @see ForbiddenImportChecker
 * @see RequiredImportChecker
 */
public class RestrictedImportChecker extends SyntaxChecker {
    private static final String GRADER_NAME = "restricted import checker";

    /**
     * Creates an import checker that tests whether all imports are
     * within a list of permitted packages.
     *
     * @param name              the name of this checker
     * @param maxPoints         the number of points to award if none are included
     * @param permittedPackages permitted package names
     */
    public RestrictedImportChecker(
            final String name,
            final double maxPoints,
            final List<String> permittedPackages) {
        super(name, maxPoints, null);
        adapter = new ImportCheckerAdapter(permittedPackages);
    }

    /**
     * Creates an import checker with the default name that tests whether all
     * imports are within a list of permitted packages.
     *
     * @param maxPoints         the number of points to award if none are included
     * @param permittedPackages permitted package names
     */
    public RestrictedImportChecker(
            final double maxPoints,
            final List<String> permittedPackages) {
        this(GRADER_NAME, maxPoints, permittedPackages);
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
        private final List<String> permittedPackages;
        private final Set<String> foundForbiddenPackages = new HashSet<>();

        private ImportCheckerAdapter(final List<String> permittedPackages) {
            this.permittedPackages = permittedPackages;
        }

        private static String importToPackageName(String importString) {
            return importString.substring(0, importString.lastIndexOf('.'));
        }

        @Override
        public void visit(final ImportDeclaration importDecl, final List<Result> results) {
            String importName = importDecl.getNameAsString();
            try {
                for (String pkg : permittedPackages) {
                    if (importName.startsWith(pkg)) {
                        return;
                    }
                }
                foundForbiddenPackages.add(importToPackageName(importName));
            } finally {
                super.visit(importDecl, results);
            }
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
