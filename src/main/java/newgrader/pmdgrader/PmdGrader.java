package newgrader.pmdgrader;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.lang.LanguageRegistry;

import newgrader.Result;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A grader that makes use of the linked <a href="https://docs.pmd-code.org/latest/index.html">
 * PMD Source Code Analyzer Project</a>.
 */
public class PmdGrader {
    private final double penaltyPerViolation;
    private final double maxPenalty;
    private final PMDConfiguration configuration;

    /**
     * Creates a PMD-based grader.
     *
     * @param ruleSet             the name of a rule set
     * @param penaltyPerViolation the penalty per violation, which should be a
     *                            positive number
     * @param maxPenalty          the maximum penalty
     */
    public PmdGrader(String ruleSet, double penaltyPerViolation, double maxPenalty) {
        this.penaltyPerViolation = penaltyPerViolation;
        this.maxPenalty = maxPenalty;

        // Set up configuration.
        configuration = new PMDConfiguration();
        configuration.setDefaultLanguageVersion(LanguageRegistry.findLanguageByTerseName("java").getVersion("17"));
        configuration.addRuleSet(ruleSet);
        configuration.setIgnoreIncrementalAnalysis(true);
    }

    /**
     * Grades any files at the specified path.
     *
     * @param path a path to a file or directory
     * @return a single result
     * @throws java.io.IOException if an I/O exception occurs
     * @see net.sourceforge.pmd.lang.document.FileCollector#addFileOrDirectory(Path)
     */
    public List<Result> grade(Path path) throws IOException {
        try (PmdAnalysis analysis = PmdAnalysis.create(configuration)) {
            analysis.files().addFileOrDirectory(path);
            Report report = analysis.performAnalysisAndCollectReport();
            // System.out.println didn't work here but System.err.println did.
            return produceResults(report);
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
            results.add(Result.makeFailure(
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

    public static void main(String[] args) throws URISyntaxException, IOException {
        URL fileURL = PmdGrader.class.getClassLoader().getResource("Main.java");
        // fileURL will be null if the file cannot be found.
        Path dirPath = Paths.get(fileURL.toURI()).getParent();
        PmdGrader grader = new PmdGrader("category/java/documentation.xml", .5, 2.0);
        List<Result> results = grader.grade(dirPath);
        System.out.println(results.get(0));
    }
}
