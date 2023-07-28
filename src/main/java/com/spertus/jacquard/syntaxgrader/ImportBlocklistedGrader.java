package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.ImportDeclaration;

import java.util.*;

/**
 * Checks whether imports on a package blocklist are present. This is
 * one of several graders checking imports.
 *
 * @see ImportDisallowedGrader
 * @see ImportRequiredGrader
 */
public class ImportBlocklistedGrader extends ImportForbiddenGrader {
    private static final String GRADER_NAME = "forbidden import grader";
    private final Set<String> blocklistedPackages;

    /**
     * Creates an import grader that tests whether there are any imports
     * involving blocklisted (forbidden) packages.
     *
     * @param name                the name of this checker
     * @param maxPoints           the number of points to award if none are included
     * @param blocklistedPackages blocklisted package names
     */
    public ImportBlocklistedGrader(
            final String name,
            final double maxPoints,
            final List<String> blocklistedPackages) {
        super(name, maxPoints);
        this.blocklistedPackages = new HashSet<>(blocklistedPackages);
        adapter = new ImportBlocklistAdapter();
    }

    /**
     * Creates an import grader with a default name that tests whether there
     * are any imports involving blocklisted (forbidden) packages.
     *
     * @param maxPoints           the number of points to award if none are included
     * @param blocklistedPackages blocklisted package names
     */
    public ImportBlocklistedGrader(
            final double maxPoints,
            final List<String> blocklistedPackages) {
        this(GRADER_NAME, maxPoints, blocklistedPackages);
    }

    private class ImportBlocklistAdapter extends ImportForbiddenGrader.ImportCheckerAdapter { // NOPMD
        @Override
        boolean isImportForbidden(ImportDeclaration importDecl) {
            return blocklistedPackages.contains(importToPackageName(importDecl));
        }
    }
}
