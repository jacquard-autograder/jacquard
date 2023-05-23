package newgrader.pmdgrader;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.renderers.*;
import newgrader.Result;
import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

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
        Renderer renderer = new MinimalRenderer();

        // Set up analysis.
        try (PmdAnalysis analysis = PmdAnalysis.create(configuration)) {
            analysis.files().addFile(futPath);
            analysis.addRenderer(renderer);

            // Perform analysis.
            analysis.performAnalysis();
            // System.out.println didn't work here but System.err.println did.

            System.err.println(renderer);
        }
        return null;
    }

    private Document parseReport(String report) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(report));
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (ParserConfigurationException e) {
            // This seems unlikely.
            throw new RuntimeException(e);
        } catch (IOException e) {
            // Very unlikely to happen, since InputSource is a string, not a file
            throw new RuntimeException(e);
        } catch (SAXException e) {
            // I think this would happen if the report isn't legal XML, which is
            // most likely because it's empty.
            throw new RuntimeException(e);
        }
    }

    /*
            beginline="2" endline="4" begincolumn="19" endcolumn="5" rule="CommentRequired" ruleset="Documentation" class="Main" method="main" externalInfoUrl="https://pmd.github.io/pmd-6.55.0/pmd_rules_java_documentation.html#commentrequired" priority="3">
                Public method and constructor comments are required
     */
    private String getValueOrDefault(Node node, String attr, String def) {
        Node value = node.getAttributes().getNamedItem(attr);
        if (value == null) {
            return def;
        } else {
            return value.getNodeValue();
        }
    }

    private String getValueOrNull(Node node, String attr) {
        return getValueOrDefault(node, attr, null);
    }

    private String getValueOrEmpty(Node node, String attr) {
        return getValueOrDefault(node, attr, "");
    }

    private String violationToMessage(Node node, String filename) {
        return String.format("%s:%s-%s\n%s\n",
                filename,
                getValueOrEmpty(node, "beginLine"),
                getValueOrEmpty(node, "endLine"),
                node.getFirstChild().getTextContent().trim());
    }

    private List<Result> produceResults(String report) {
        Document doc = parseReport(report);
        System.err.println(doc);
        Node filenameNode = doc.getFirstChild().getChildNodes().item(1);
        NodeList errors = doc.getElementsByTagName("error");
        NodeList violations = doc.getElementsByTagName("violation");

        List<Result> results = new ArrayList<>();
        // doc.getElementsByTagName("violation").item(0).getAttributes().getNamedItem("rule")
        if (errors.getLength() > 0) {
            String filename = getValueOrEmpty(filenameNode, "filename");
            String msg = getValueOrEmpty(filenameNode, "msg");
            results.add(Result.makeFailure(
                    "Error during static analysis",
                    maxPenalty,
                    msg
            ));
            return results;
        }
        if (violations.getLength() > 0) {
            String filename = doc.getFirstChild().getChildNodes().item(1).getAttributes().getNamedItem("name").getTextContent();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < violations.getLength(); i++) {
                sb.append(violationToMessage(violations.item(i), filename));
                sb.append("\n");
            }
            String message = String.format("""
                            Violations: %d
                            %s
                            """,
                    violations.getLength(),
                    sb);
            results.add(Result.makeResult(
                    "Problems identified during static analysis",
                    Math.max(maxPenalty - violations.getLength() * penaltyPerViolation, 0),
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
        grader.grade(filePath);
    }
}
