package newgrader.pmdgrader;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import newgrader.Result;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.List;

// https://docs.pmd-code.org/latest/pmd_userdocs_tools_java_api.html
public class PmdGrader {
    private final double penaltyPerViolation;
    private final double maxPenalty;
    private final PMDConfiguration configuration;
    private final PmdAnalysis analysis;

    public PmdGrader(double penaltyPerViolation, double maxPenalty, Path path) {
        this.penaltyPerViolation = penaltyPerViolation;
        this.maxPenalty = maxPenalty;

        // Set up configuration.
        configuration = new PMDConfiguration();
        configuration.setDefaultLanguageVersion(LanguageRegistry.findLanguageByTerseName("java").getVersion("17"));
        configuration.setReportFormat("xml");
        // configuration.setReportFile("/dev/null");
        Writer rendererOutput = new StringWriter();
        XMLRenderer xml = new XMLRenderer("UTF-8");
        xml.setWriter(rendererOutput);

        configuration.addRuleSet("category/java/documentation.xml");

        // Set up analysis.
        analysis = PmdAnalysis.create(configuration);
        analysis.files().addFile(path);
        // analysis.addRuleSet(RuleSet.forSingleRule(rule));
        analysis.performAnalysis();
        String output = rendererOutput.toString();
        System.out.println(output);

    }


    public static void main(String[] args) throws RuleSetNotFoundException, URISyntaxException {

        // https://stackoverflow.com/a/45782699/631051
        URL fileURL = PmdGrader.class.getClassLoader().getResource("Main.java");
        Path filePath = Paths.get(fileURL.toURI());
        new PmdGrader(.5, 2.0, filePath);
    }
}
