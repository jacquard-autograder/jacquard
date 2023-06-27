package newgrader.coverage;

import newgrader.common.*;
import newgrader.exceptions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * A grader that uses Jacoco to measure code coverage of tests.
 */
public class CodeCoverageGrader extends Grader {
    private static final String GRADER_NAME = "Code Coverage Grader";
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

    /**
     * Creates a code coverage grader with the given name and scorer.
     *
     * @param name the name
     * @param scorer a scorer, which converts the outcome to a point value
     */
    public CodeCoverageGrader(String name, Scorer scorer) {
        super(name);
        this.scorer = scorer;
    }

    /**
     * Creates a code coverage grader with the given scorer.
     *
     * @param scorer a scorer, which converts the outcome to a point value
     */
    public CodeCoverageGrader(Scorer scorer) {
        this(GRADER_NAME, scorer);
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
            args.add("-Dstudent.srcdir=" + target.toDirectory());
            MavenInterface.runMavenProcess(args);
            ClassInfo classInfo = getClassInfo(target);
            return List.of(scorer.getResult(classInfo));
        } catch (DependencyException e) {
            return makeExceptionResultList(
                    new InternalException(
                            "Exception was thrown when running autograder", e));
        }
    }
}
