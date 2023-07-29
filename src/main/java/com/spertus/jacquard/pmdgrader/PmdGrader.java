package com.spertus.jacquard.pmdgrader;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.exceptions.ClientException;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.lang.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * A grader that makes use of the linked <a href="https://docs.pmd-code.org/latest/index.html">
 * PMD Source Code Analyzer Project</a>.
 */
public final class PmdGrader extends Grader {
    private static final String GRADER_NAME = "PMD Grader";
    private static final String JAVA_VERSION = "17";
    private final double penaltyPerViolation;
    private final double maxPenalty;
    private final PMDConfiguration configuration;

    // These are used only if createFromRules() is used.
    private String ruleSetPath;
    private String[] ruleNames;

    private static PMDConfiguration createConfiguration() {
        final PMDConfiguration config = new PMDConfiguration();
        final PMDConfiguration configuration = new PMDConfiguration();
        final LanguagePropertyBundle properties =
                configuration.getLanguageProperties(LanguageRegistry.PMD.getLanguageById("java"));
        properties.setLanguageVersion(JAVA_VERSION);
        return config;
    }

    private PmdGrader(
            final double penaltyPerViolation,
            final double maxPenalty,
            final String... ruleSetPaths) {
        super(GRADER_NAME);
        this.penaltyPerViolation = penaltyPerViolation;
        this.maxPenalty = maxPenalty;
        configuration = createConfiguration();

        // Build (and discard) analysis here to fail fast if any paths are invalid.
        try (PmdAnalysis analysis = PmdAnalysis.create(configuration)) {
            final RuleSetLoader loader = analysis.newRuleSetLoader();
            for (final String ruleSetPath : ruleSetPaths) {
                try {
                    loader.loadFromResource(ruleSetPath);
                } catch (RuleSetLoadException e) {
                    throw new ClientException("Unable to load rule set " + ruleSetPath, e);
                }
                Arrays.stream(ruleSetPaths).forEach(configuration::addRuleSet);
            }
        }
    }

    private PmdGrader(
            final double penaltyPerViolation,
            final double maxPenalty,
            final String ruleSetPath,
            final String... ruleNames) {
        super(GRADER_NAME);
        this.penaltyPerViolation = penaltyPerViolation;
        this.maxPenalty = maxPenalty;
        this.ruleSetPath = ruleSetPath;
        this.ruleNames = ruleNames;
        configuration = createConfiguration();

        // Build List<RuleSet> here to fail fast if the rule set path or a rule
        // name is invalid.
        createAnalysisWithRuleNames().close();
    }

    // throws ClientException
    private PmdAnalysis createAnalysis() {
        if (ruleSetPath == null) {
            return PmdAnalysis.create(configuration);
        } else {
            return createAnalysisWithRuleNames();
        }
    }

    // It is the caller's responsibility to call close().
    // throws ClientException
    private PmdAnalysis createAnalysisWithRuleNames() {
        try {
            final PmdAnalysis analysis = PmdAnalysis.create(configuration);
            final RuleSetLoader loader = analysis.newRuleSetLoader();
            final RuleSet ruleSet = loader.loadFromResource(ruleSetPath);
            for (final String ruleName : ruleNames) {
                final Rule rule = ruleSet.getRuleByName(ruleName);
                if (rule == null) {
                    throw new ClientException(String.format(
                            "Did not find rule %s in %s",
                            rule, ruleSetPath));
                }
                analysis.addRuleSet(RuleSet.forSingleRule(rule));
            }
            return analysis;
        } catch (RuleSetLoadException e) {
            // can be thrown by RuleSetLoader.loadFromResource()
            throw new ClientException("Unable to load rule set " + ruleSetPath, e);
        }
    }

    /**
     * Creates a PMD-based grader for the specified rule sets. The ruleSetPaths
     * argument should be one or more paths to rule sets in <a
     * href="https://github.com/pmd/pmd/tree/master/pmd-java/src/main/resources">
     * the PMD resource directory</a> (such as "category/java/quickstart.xml")
     * or in one of the client project's resource directories.
     *
     * @param penaltyPerViolation the penalty per violation, which should be a
     *                            positive number
     * @param maxPenalty          the maximum penalty
     * @param ruleSetPaths        the path to one or more rule sets
     * @return new PMD grader
     * @throws ClientException if any rule set path is invalid
     */
    public static PmdGrader createFromRuleSetPaths(
            final double penaltyPerViolation,
            final double maxPenalty,
            final String... ruleSetPaths) {
        return new PmdGrader(penaltyPerViolation, maxPenalty, ruleSetPaths);
    }

    /**
     * Creates a PMD-based grader for the specified rules. The ruleSetPath
     * argument should be the path to a rule set in <a
     * href="https://github.com/pmd/pmd/tree/master/pmd-java/src/main/resources">
     * the PMD resource directory</a> (such as "category/java/quickstart.xml")
     * or in one of the client project's resource directories. The ruleNames
     * argument should give rule names without a preceding path, such as
     * "MissingOverride".
     *
     * @param penaltyPerViolation the penalty per violation, which should be a
     *                            positive number
     * @param maxPenalty          the maximum penalty
     * @param ruleSetPath         the path to a rule set
     * @param ruleNames           the names of the rules in the rule set to use
     * @return new PMD grader
     * @throws ClientException if any rule set path is invalid or a rule cannot be found
     */
    public static PmdGrader createFromRules(
            final double penaltyPerViolation,
            final double maxPenalty,
            final String ruleSetPath,
            final String... ruleNames) {
        return new PmdGrader(penaltyPerViolation, maxPenalty, ruleSetPath, ruleNames);
    }

    @Override
    public Callable<List<Result>> getCallable(final Target target) {
        return () -> {
            try (PmdAnalysis analysis = createAnalysis()) {
                final boolean added = analysis.files().addFileOrDirectory(target.toPath());
                if (!added) {
                    throw new ClientException("File or directory cannot be found: " + target.toPathString());
                }
                final Report report = analysis.performAnalysisAndCollectReport();
                return produceResults(report);
            } catch (IOException e) {
                return makeExceptionResultList(
                        new ClientException("File or directory cannot be found: " + target.toPathString()));
            }
        };
    }

    private String violationToString(final RuleViolation violation) {
        final String lineString = violation.getBeginLine() == violation.getEndLine() ?
                "line " + violation.getBeginLine() :
                String.format("lines %d-%d", violation.getBeginLine(), violation.getEndLine());
        return String.format("Problem: %s (%s)\n%s: %s\n",
                violation.getDescription(),
                violation.getRule().getExternalInfoUrl(),
                violation.getFileId(),
                lineString);
    }

    private List<Result> produceResults(final Report report) {
        final List<Result> results = new ArrayList<>();
        final List<Report.ProcessingError> errors = report.getProcessingErrors();

        if (!errors.isEmpty()) {
            // For now, just print information about the first error.
            final Report.ProcessingError error = errors.get(0);
            results.add(makeFailureResult(
                    maxPenalty,
                    error.getMsg() + ": " + error.getError().getCause().getMessage()));
            return results;
        }

        final List<RuleViolation> violations = report.getViolations();
        if (violations.isEmpty()) {
            results.add(Result.makeSuccess("Static analysis (PMD)", maxPenalty, "No problems detected"));
        } else {
            final String message = violations.stream()
                    .map(this::violationToString)
                    .collect(Collectors.joining("\r\n"));
            results.add(makePartialCreditResult(
                    Math.max(maxPenalty - violations.size() * penaltyPerViolation, 0),
                    maxPenalty,
                    message));
        }

        return results;
    }
}
