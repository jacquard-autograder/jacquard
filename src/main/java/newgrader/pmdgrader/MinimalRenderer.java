package newgrader.pmdgrader;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.renderers.AbstractIncrementingRenderer;
import org.apache.commons.io.output.NullWriter;

import java.io.*;
import java.util.*;

public class MinimalRenderer extends AbstractIncrementingRenderer {
    public static final String NAME = "minimal";
    private Report report;
    private List<RuleViolation> violations;

    public MinimalRenderer() {
        super(NAME, "minimal renderer for Java integration");
        writer = NullWriter.nullWriter();
    }

    // Getters
    public Report getReport() {
        return report;
    }

    public List<RuleViolation> getViolations() {
        return violations;
    }

    // AbstractIncrementingRenderer
    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
        this.violations = new ArrayList<>();
        violations.forEachRemaining(this.violations::add);
    }

    @Override
    public void renderFileReport(Report report) throws IOException {
        super.renderFileReport(report);
        this.report = report;
    }

    @Override
    public String defaultFileExtension() {
        return "pojo";
    }
}
