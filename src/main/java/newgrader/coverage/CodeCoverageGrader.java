package newgrader.coverage;

import newgrader.Result;
import newgrader.exceptions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CodeCoverageGrader {
    private static final String PATH_TO_MAVEN_WINDOWS = "C:/Program Files/apache-maven-3.6.3/bin/mvn.cmd";
    private static final String PATH_TO_MAVEN_LINUX = "mvn";
    private static final String PATH_TO_JACOCO_POM = "pom-jacoco.xml";
    private static final List<String> JACOCO_COMMAND_LINE_ARGS = List.of(
            "-f",
            PATH_TO_JACOCO_POM,
            "clean",
            "test");
    private static final String PATH_TO_JACOCO_CSV = "target/site/jacoco/jacoco.csv";
    // Jacoco CSV file
    private static final int PACKAGE_FIELD = 1;
    private static final int CLASS_FIELD = 2;
    private static final int NUM_FIELDS = 13;

    private final String packageName;
    private final String className;
    private final Scorer scorer;

    public CodeCoverageGrader(String packageName, String className, Scorer scorer) {
        this.packageName = packageName;
        this.className = className;
        this.scorer = scorer;
    }

    private ClassInfo getClassInfo() throws AutograderException {
        try {
            Path path = Paths.get(PATH_TO_JACOCO_CSV);

            // It is not really necessary to read in all lines,
            // so performance could be improved here.
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] fields = line.split(",");
                if (fields.length == NUM_FIELDS) {
                    if (fields[PACKAGE_FIELD].equals(packageName) && fields[CLASS_FIELD].equals(className)) {
                        return new ClassInfo(fields);
                    }
                }
            }
        } catch (IOException e) {
            throw new DependencyException("Jacoco output not found with base dir " +  System.getProperty("user.dir"), e);
        }
        throw new ClientException(
                String.format("No class info found for %s.%s", packageName, className));
    }

    public Result grade() {
        List<String> command = new ArrayList<>(JACOCO_COMMAND_LINE_ARGS.size() + 1);
        command.add(System.getProperty("os.name").startsWith("Windows") ? PATH_TO_MAVEN_WINDOWS : PATH_TO_MAVEN_LINUX);
        command.addAll(JACOCO_COMMAND_LINE_ARGS);
        ProcessBuilder pb = new ProcessBuilder(command);
        // pb.inheritIO();
        try {
            Process p = pb.start();
            p.waitFor();
            /*
            int exitCode = p.waitFor();
            System.out.println("command: " + command);
            System.out.println("exitCode: " + exitCode);
             */
            ClassInfo info = getClassInfo();
            return scorer.getResult(info);
        } catch (AutograderException | IOException | InterruptedException e) {
            return Result.makeException(
                    "Exception was thrown when running autograder", 0, e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scorer scorer = new LinearScorer(.5, 10);
        CodeCoverageGrader grader = new CodeCoverageGrader("student", "PrimeChecker", scorer);
        System.out.println(grader.grade());

    }
}
