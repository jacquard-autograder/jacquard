package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.spertus.jacquard.common.Result;

import java.util.*;

/**
 * Abstract superclass of graders that check whether there are any imports
 * of packages that are not allowed.
 */
public abstract class ImportForbiddenGrader extends SyntaxCheckGrader {
    /**
     * Creates an import grader that tests whether there are any imports
     * involving blocklisted (forbidden) packages.
     *
     * @param name     the name of this checker
     * @param maxScore the number of points to award if none are included
     */
    protected ImportForbiddenGrader(
            final String name,
            final double maxScore) {
        super(name, maxScore, null);
        // adapter is set by concrete subclass
    }

    @Override
    public void initialize() {
        ((ImportCheckerAdapter) adapter).initialize();
    }

    @Override
    public void finalizeResults(final List<Result> results) {
        ((ImportCheckerAdapter) adapter).finalizeResults(results);
    }

    /**
     * Abstract superclass of adapters testing whether forbidden imports appear.
     */
    protected abstract class ImportCheckerAdapter extends VoidVisitorAdapter<List<Result>> { // NOPMD
        private final Set<String> foundForbiddenPackages = new HashSet<>();

        /**
         * Constructs an adapter.
         */
        protected ImportCheckerAdapter() {
            super();
        }

        private void initialize() { // NOPMD (false positive)
            foundForbiddenPackages.clear();
        }

        /**
         * Converts an import to a package name.
         *
         * @param importDecl an import declaration
         * @return the package name
         */
        protected static String importToPackageName(final ImportDeclaration importDecl) {
            final String name = importDecl.getNameAsString();
            if (importDecl.isAsterisk()) {
                return name;
            }
            return name.substring(0, name.lastIndexOf('.'));
        }

        /**
         * Checks whether the given import declaration is forbidden.
         *
         * @param importDecl the import declaration
         * @return true if it is forbidden, false if it is permitted
         */
        abstract protected boolean isImportForbidden(ImportDeclaration importDecl);

        @Override
        public void visit(final ImportDeclaration importDecl, final List<Result> results) {
            if (isImportForbidden(importDecl)) {
                foundForbiddenPackages.add(importDecl.getNameAsString());
            }
            super.visit(importDecl, results);
        }

        private void finalizeResults(List<Result> results) {  // NOPMD (false positive)
            if (foundForbiddenPackages.isEmpty()) {
                results.add(makeSuccessResult("No forbidden packages imported."));
            } else {
                results.add(makeFailingResult("Forbidden packages imported: " +
                        String.join(", ", foundForbiddenPackages)));
            }
        }
    }
}
