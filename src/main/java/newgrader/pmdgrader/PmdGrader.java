package newgrader.pmdgrader;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.lang.LanguageRegistry;

import newgrader.Result;

import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

// https://docs.pmd-code.org/latest/pmd_userdocs_tools_java_api.html
public class PmdGrader {
    private final double penaltyPerViolation;
    private final double maxPenalty;
    private final PMDConfiguration configuration;
    // private final PmdAnalysis analysis;

    public PmdGrader(double penaltyPerViolation, double maxPenalty) {
        this.penaltyPerViolation = penaltyPerViolation;
        this.maxPenalty = maxPenalty;

        // Set up configuration.
        configuration = new PMDConfiguration();
        configuration.setDefaultLanguageVersion(LanguageRegistry.findLanguageByTerseName("java").getVersion("17"));
        // configuration.setReportFormat("xml");
        configuration.addRuleSet("category/java/documentation.xml");
        configuration.setIgnoreIncrementalAnalysis(true);
    }

    public List<Result> grade(Path futPath) {
        // Set up output.
        MinimalRenderer renderer = new MinimalRenderer();

        // Set up analysis.
        try (PmdAnalysis analysis = PmdAnalysis.create(configuration)) {
            analysis.files().addFile(futPath);
            analysis.addRenderer(renderer);

            // Perform analysis.
            analysis.performAnalysis();
            // System.out.println didn't work here but System.err.println did.

            System.err.println(renderer);
        }
        return produceResults(renderer.getReport());
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

    public static void main(String[] args) throws URISyntaxException {
        // https://stackoverflow.com/a/45782699/631051
        URL fileURL = PmdGrader.class.getClassLoader().getResource("Main.java");
        // fileURL will be null if the file cannot be found.
        Path filePath = Paths.get(fileURL.toURI());
        PmdGrader grader = new PmdGrader(.5, 2.0);
        List<Result> results = grader.grade(filePath);
        System.out.println(results.get(0));
    }
}
