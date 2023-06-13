package newgrader.checkstylegrader;

import newgrader.*;
import newgrader.exceptions.DependencyException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

// Is there any benefit of running through Maven rather than directly?
public class CheckstyleGrader {
    private static final String PATH_TO_POM = "pom-checkstyle.xml";
    private static final List<String> FIRST_ARGUMENTS = List.of(
            "-f",
            PATH_TO_POM,
            "checkstyle:checkstyle"
    );
    private static final String LAST_ARGUMENT_TEMPLATE =
            "-Dcheckstyle.includes=\"%s\"";
    private static final String PATH_TO_OUTPUT = "target/checkstyle-result.xml";

    private final String ruleFile;
    private final String pathToCheck;
    private final double penalty;
    private final double maxPoints;

    public CheckstyleGrader(String ruleFile, String pathToCheck, double penalty, double maxPoints) {
        this.ruleFile = ruleFile;
        this.pathToCheck = pathToCheck;
        this.penalty = penalty;
        this.maxPoints = maxPoints;
    }

    // Taken from JGrade:CheckstyleGrader and modified
    private static String getAttributeValue(String prefix, Node attribute) {
        return attribute == null ? "" : String.format("%s%s", prefix, attribute.getNodeValue());
    }

    private static String getAttributeValue(Node attribute) {
        return getAttributeValue("", attribute);
    }

    private String getOutputForErrorNode(NamedNodeMap attributes) {
        if (attributes == null) {
            throw new InternalError();
        } else {
            Node lineAttribute = attributes.getNamedItem("line");
            Node columnAttribute = attributes.getNamedItem("column");
            Node messageAttribute = attributes.getNamedItem("message");
            String errorTypeAttribute = getAttributeValue(attributes.getNamedItem("source"));
            /*
            if (errorTypeAttribute.contains(".")) {
                String[] split = errorTypeAttribute.split("\\.");
                errorTypeAttribute = split[split.length - 1];
                if (!this.errorTypes.containsKey(errorTypeAttribute)) {
                    this.errorTypes.put(errorTypeAttribute, 1);
                } else {
                    this.errorTypes.put(errorTypeAttribute, (Integer)this.errorTypes.get(errorTypeAttribute) + 1);
                }
            }

             */

            return String.format("\t%-20s - %s [%s]\n", getAttributeValue("line: ", lineAttribute) + getAttributeValue(", column", columnAttribute), getAttributeValue(messageAttribute), errorTypeAttribute);
        }
    }

    private int addOutputForFileNode(StringBuilder sb, Node elementNode) {
        String fullPath = elementNode.getAttributes().getNamedItem("name").toString();
        String fileName = fullPath.substring(fullPath.lastIndexOf(System.getProperty("file.separator")) + 1, fullPath.length() - 1);
        NodeList errorNodes = ((Element) elementNode).getElementsByTagName("error");
        if (errorNodes.getLength() > 0) {
            sb.append(fileName + ":\n");
        }

        for (int i = 0; i < errorNodes.getLength(); ++i) {
            sb.append(this.getOutputForErrorNode(errorNodes.item(i).getAttributes()));
        }

        return errorNodes.getLength();
    }

    // Code modeled after JGrade:CheckstyleGrader
    public Result grade() {
        List<String> arguments = new ArrayList<>();
        arguments.addAll(FIRST_ARGUMENTS);
        arguments.add(String.format(LAST_ARGUMENT_TEMPLATE, pathToCheck));
        MavenInterface.runMavenProcess(arguments);
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            File file = new File(PATH_TO_OUTPUT);
            Document doc = builder.parse(file);
            NodeList filesWithErrors = doc.getElementsByTagName("file");
            int numErrors = 0;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < filesWithErrors.getLength(); i++) {
                Node fileNode = filesWithErrors.item(i);
                numErrors += addOutputForFileNode(sb, fileNode);
            }
            if (numErrors == 0) {
                return Result.makeSuccess("Checkstyle", maxPoints, "No violations");
            } else {
                double score = Math.min(0.0, maxPoints - numErrors * penalty);
                return Result.makeResult("Checkstyle", score, maxPoints, sb.toString());
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            return Result.makeError("Internal error when running Checkstyle", e);
        }
    }

    public static void main(String[] args) {
        // I have not found a way to specify a single file, either here or
        // at the git bash command line.
        //    Result result = new CheckstyleGrader("ignored", "C:\\Users\\ellen\\IdeaProjects\\GroovyNewGrader\\src\\main\\java\\client\\buggy\\ArrayFlist.java", 0, 0).grade();
        //     Result result = new CheckstyleGrader("ignored", "/c/Users/ellen/IdeaProjects/GroovyNewGrader/src/main/java/client/buggy/ArrayFlist.java", 0, 0).grade();
        //     Result result = new CheckstyleGrader("ignored", "src/main/java/client/buggy/ArrayFlist.java", 0, 0).grade();
        //      Result result = new CheckstyleGrader("ignored", "src\\main\\java\\client\\buggy\\ArrayFlist.java", 0, 0).grade();
        Result result = new CheckstyleGrader("ignored", "foo.java", 0, 0).grade();

        System.out.println(result);
    }
}
