package newgrader.pmdgrader;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.lang.*;

import newgrader.common.*;
import newgrader.exceptions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A grader that makes use of the linked <a href="https://docs.pmd-code.org/latest/index.html">
 * PMD Source Code Analyzer Project</a>.
 */
public class PmdGrader extends Grader {
    private static final String GRADER_NAME = "PMD Grader";
    private static final String JAVA_VERSION = "17";
    private final double penaltyPerViolation;
    private final double maxPenalty;
    private final PMDConfiguration configuration;

    // These are used only if createFromRules() is used.
    private String ruleSetPath;
    private String[] ruleNames;

    private static PMDConfiguration createConfiguration() {
        PMDConfiguration config = new PMDConfiguration();
        PMDConfiguration configuration = new PMDConfiguration();
        LanguagePropertyBundle properties = configuration.getLanguageProperties(LanguageRegistry.PMD.getLanguageById("java"));
        properties.setLanguageVersion(JAVA_VERSION);
        return config;
    }

    private PmdGrader(double penaltyPerViolation, double maxPenalty, String[] ruleSetPaths)
            throws ClientException {
        super(GRADER_NAME);
        this.penaltyPerViolation = penaltyPerViolation;
        this.maxPenalty = maxPenalty;
        configuration = createConfiguration();

        // Build (and discard) analysis here to fail fast if any paths are invalid.
        try (PmdAnalysis analysis = PmdAnalysis.create(configuration)) {
            RuleSetLoader loader = analysis.newRuleSetLoader();
            for (String ruleSetPath : ruleSetPaths) {
                try {
                    loader.loadFromResource(ruleSetPath);
                } catch (RuleSetLoadException e) {
                    throw new ClientException("Unable to load rule set " + ruleSetPath, e);
                }
                Arrays.stream(ruleSetPaths).forEach(configuration::addRuleSet);
            }
        }
    }

    private PmdGrader(double penaltyPerViolation, double maxPenalty, String
            ruleSetPath, String[] ruleNames) throws ClientException {
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

    private PmdAnalysis createAnalysis() throws ClientException {
        if (ruleSetPath == null) {
            return PmdAnalysis.create(configuration);
        } else {
            return createAnalysisWithRuleNames();
        }
    }

    // It is the caller's responsibility to call close().
    private PmdAnalysis createAnalysisWithRuleNames() throws ClientException {
        try {
            PmdAnalysis analysis = PmdAnalysis.create(configuration);
            RuleSetLoader loader = analysis.newRuleSetLoader();
            RuleSet ruleSet = loader.loadFromResource(ruleSetPath);
            for (String ruleName : ruleNames) {
                Rule rule = ruleSet.getRuleByName(ruleName);
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
     * the PMD resource directory</a> (such as "category/java/documentation.xml")
     * or in one of the client project's resource directories.
     *
     * @param penaltyPerViolation the penalty per violation, which should be a
     *                            positive number
     * @param maxPenalty          the maximum penalty
     * @param ruleSetPaths        the path to one or more rule sets
     * @throws ClientException if any rule set path is invalid
     */
    public static PmdGrader createFromRuleSetPaths(
            double penaltyPerViolation, double maxPenalty, String... ruleSetPaths)
            throws ClientException {
        return new PmdGrader(penaltyPerViolation, maxPenalty, ruleSetPaths);
    }

    /**
     * Creates a PMD-based grader for the specified rules. The ruleSetPath
     * argument should be the path to a rule set in <a
     * href="https://github.com/pmd/pmd/tree/master/pmd-java/src/main/resources">
     * the PMD resource directory</a> (such as "category/java/documentation.xml")
     * or in one of the client project's resource directories.
     *
     * @param penaltyPerViolation the penalty per violation, which should be a
     *                            positive number
     * @param maxPenalty          the maximum penalty
     * @param ruleSetPath         the path to a rule sets
     * @param ruleNames           the names of the rules in the rule set to use
     * @throws ClientException if any rule set path is invalid or a rule cannot be found
     */
    public static PmdGrader createFromRules(
            double penaltyPerViolation, double maxPenalty, String
            ruleSetPath, String... ruleNames) throws ClientException {
        return new PmdGrader(penaltyPerViolation, maxPenalty, ruleSetPath, ruleNames);
    }

    @Override
    public List<Result> grade(Target target) {
        try (PmdAnalysis analysis = createAnalysis()) {
            boolean added = analysis.files().addFileOrDirectory(target.toPath());
            if (!added) {
                throw new ClientException("File or directory cannot be found: " + target.toPathString());
            }
            Report report = analysis.performAnalysisAndCollectReport();
            return produceResults(report);
        } catch (IOException e) {
            throw new ClientException("File or directory cannot be found: " + target.toPathString());
        }
    }

    private String violationToString(RuleViolation violation) {
        return String.format("Problem: %s (%s)\n%s: lines %s-%s\n",
                violation.getRule().getMessage(),
                violation.getRule().getExternalInfoUrl(),
                violation.getFilename(),
                violation.getBeginLine(),
                violation.getEndLine());
    }

    private List<Result> produceResults(Report report) {
        List<Result> results = new ArrayList<>();
        List<Report.ProcessingError> errors = report.getProcessingErrors();
        List<RuleViolation> violations = report.getViolations();

        if (!errors.isEmpty()) {
            // For now, just print information about the first error.
            Report.ProcessingError error = errors.get(0);
            results.add(Result.makeTotalFailure(
                    "Error during static analysis",
                    maxPenalty,
                    error.getMsg() + ": " + error.getError().getCause().getMessage()));
            return results;
        }

        if (violations.isEmpty()) {
            results.add(Result.makeSuccess("Static analysis (PMD)", maxPenalty, "No problems detected"));
        } else {
            String message = violations.stream()
                    .map(this::violationToString)
                    .collect(Collectors.joining("\r\n"));
            results.add(Result.makeResult(
                    "Problems identified during static analysis",
                    Math.max(maxPenalty - violations.size() * penaltyPerViolation, 0),
                    maxPenalty,
                    message));
        }

        return results;
    }
}
