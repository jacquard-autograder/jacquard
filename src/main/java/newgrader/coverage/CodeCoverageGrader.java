package newgrader.coverage;

import newgrader.common.*;
import newgrader.exceptions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CodeCoverageGrader extends Grader {
    private static final String GRADER_NAME = "Code Coverage Grader";
    private static final Path STUDENT_SRC_ROOT = StudentPathStringTarget.STUDENT_SRC_ROOT; // for now
    private static final String PATH_TO_JACOCO_POM = "pom-jacoco.xml";
    private static final List<String> JACOCO_COMMAND_LINE_ARGS = List.of(
            "-f",
            PATH_TO_JACOCO_POM,
            "clean",
            "verify");

    private static final String PATH_TO_JACOCO_CSV = "target/site/jacoco/jacoco.csv";
    // Jacoco CSV file
    private static final int PACKAGE_FIELD = 1;
    private static final int CLASS_FIELD = 2;
    private static final int NUM_FIELDS = 13;

    private final Scorer scorer;

    public CodeCoverageGrader(Scorer scorer) {
        this(GRADER_NAME, scorer);
    }

    public CodeCoverageGrader(String name, Scorer scorer) {
        super(name);
        this.scorer = scorer;
    }

    private ClassInfo getClassInfo(Target target) throws AutograderException {
        try {
            Path path = Paths.get(PATH_TO_JACOCO_CSV);
            String packageName = target.toPackageName();
            String className = target.toClassName();

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
            throw new DependencyException("Jacoco output not found with base dir " + System.getProperty("user.dir"), e);
        }
        throw new ClientException(
                String.format("No class info found for %s", target.toPathString().toString()));
    }

    @Override
    public List<Result> grade(Target target) {
        try {
            List<String> args = new ArrayList<>(JACOCO_COMMAND_LINE_ARGS);
            // Whether run on Windows or Unix, Maven uses Unix-style paths.
            args.add("-Dstudent.srcdir=" + STUDENT_SRC_ROOT.toString());
            MavenInterface.runMavenProcess(args);
            ClassInfo classInfo = getClassInfo(target);
            return List.of(scorer.getResult(classInfo));
        } catch (DependencyException e) {
            return makeExceptionResultList(
                    new InternalException(
                            "Exception was thrown when running autograder", e));
        }
    }

    public static void main(String[] args) {
        Scorer scorer = new LinearScorer(.5, 10);
        CodeCoverageGrader grader = new CodeCoverageGrader(scorer);
        System.out.println(grader.grade(Target.fromStudentPathString("student/PrimeChecker.java")));

    }
}
