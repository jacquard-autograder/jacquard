package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.spertus.jacquard.common.Result;

import java.util.*;

/**
 * Checks whether any imports are found from packages that are not on an
 * explicit allowlist. This is one of several graders involving imports.
 *
 * @see ImportBlocklistedGrader
 * @see ImportRequiredGrader
 */
public class ImportDisallowedGrader extends ImportForbiddenGrader {
    private static final String GRADER_NAME = "restricted import grader";
    private final Set<String> allowedPackages;

    /**
     * Creates an import checker that tests whether all imports are
     * within a list of allowed packages.
     *
     * @param name            the name of this checker
     * @param maxPoints       the number of points to award if none are included
     * @param allowedPackages allowed package names
     */
    public ImportDisallowedGrader(
            final String name,
            final double maxPoints,
            final List<String> allowedPackages) {
        super(name, maxPoints);
        this.allowedPackages = new HashSet<>(allowedPackages);
        adapter = new ImportCheckerAdapter();
    }

    /**
     * Creates an import checker with the default name that tests whether all
     * imports are within a list of allowed packages.
     *
     * @param maxPoints       the number of points to award if none are included
     * @param allowedPackages allowed package names
     */
    public ImportDisallowedGrader(
            final double maxPoints,
            final List<String> allowedPackages) {
        this(GRADER_NAME, maxPoints, allowedPackages);
    }

    private class ImportCheckerAdapter extends ImportForbiddenGrader.ImportCheckerAdapter { // NOPMD
        @Override
        boolean isImportForbidden(ImportDeclaration importDecl) {
            String name = importToPackageName(importDecl);
            return !allowedPackages.contains(name);
        }
    }
}
