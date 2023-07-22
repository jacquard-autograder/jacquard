package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.spertus.jacquard.common.Result;

import java.util.List;

/**
 * Checks whether a specified library is imported.
 */
public class ImportChecker extends SyntaxChecker {
    private final double maxPoints;
    private final String substring;
    private final boolean required;

    /**
     * Creates an import checker that awards points if a required import is
     * found or if a forbidden import is not found. Currently, this does not
     * support wildcards, so the substring "java.util.List" would not be
     * matched by "import java.util.*;".
     *
     * @param name  the name of this checker
     * @param maxPoints the number of points at stake
     * @param substring the substring to check for
     * @param required true if the substring is required, false if it is forbidden
     */
    public ImportChecker(String name, double maxPoints, String substring, boolean required) {
        super(name, maxPoints, null);
        this.maxPoints = maxPoints;
        this.substring = substring;
        this.required = required;
        adapter = new ImportCheckerAdapter();
    }

    @Override
    public double getTotalMaxScore() {
        return maxPoints;
    }

    @Override
    public void finalizeResults(List<Result> results) {
        if (!results.isEmpty()) {
            return;
        }
        results.add(
                required ?
                        makeFailureResult(maxPoints, "Required import not found")
                        :
                        makeSuccessResult(maxPoints, "Forbidden import not found"));
    }

    private class ImportCheckerAdapter extends VoidVisitorAdapter<List<Result>> {
        @Override
        public void visit(ImportDeclaration importDecl, List<Result> results) {
            if (!results.isEmpty()) {
                return;
            }
            if (importDecl.getName().toString().contains(substring)) {
                if (required) {
                    results.add(makeSuccessResult(maxPoints, "required import found"));
                } else {
                    results.add(makeFailureResult(maxPoints, "Forbidden import found"));
                }
            }
        }
    }
}
