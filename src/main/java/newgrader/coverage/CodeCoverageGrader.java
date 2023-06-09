package newgrader.coverage;

import newgrader.Result;
import newgrader.exceptions.*;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class CodeCoverageGrader {
    public static final String PATH_TO_JACOCO_CSV = "target/site/jacoco/jacoco.csv";
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
            throw new DependencyException("Jacoco output not found", e);
        }
        throw new ClientException(
                String.format("No class info found for %s.%s", packageName, className));
    }

    public Result grade() {
        ProcessBuilder pb = new ProcessBuilder(
                "C:/Program Files/apache-maven-3.6.3/bin/mvn.cmd",
                "clean",
                "test");
        try {
            Process p = pb.start();
            // String result = new String(p.getInputStream().readAllBytes());
            p.waitFor();
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
