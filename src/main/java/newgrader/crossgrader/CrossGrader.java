package newgrader.crossgrader;

import newgrader.common.Result;
import newgrader.exceptions.ClientException;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherFactory;

import java.io.InputStream;
import java.util.*;

import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

/**
 * A grader for running student-provided tests against multiple implementations.
 * This can be used to test whether student tests fail intentionally buggy
 * code (which is good) or pass correct code.
 */
public class CrossGrader {
    private static final String DELIM = "\\s*,\\s*";
    private final Class<?> generalizedTestClass;
    // The next four instance variables are initialized in processCsvFile().
    private String[] methodNames;
    private String[] cutNames; // classes under test
    private double[][] points;

    /**
     * Creates a cross grader that uses the provided test class to instantiate
     * classes under test that are specified in the score input stream.
     * <p>
     * The test class must have a constructor that takes the fully-qualified
     * name of a class as its single (string) argument. This class should
     * contain JUnit tests whose names begin with the names of methods
     * under test.
     * <p>
     * The score input stream should be CSV with information about the
     * classes under test (in the header row), methods under test (in
     * the leftmost column), and maximum points for each combination.
     * A positive points value means that points should be scored if the
     * test passes, a negative points value means that points should be scored
     * if the text fails, while a points value of zero means that no points
     * should be scored for the combination. If a points value is non-zero
     * and there are no tests of the specified method, a score of 0 is given.
     * <p>
     * Consider this sample file, which might be used to check whether student
     * tests do not report bugs on instructor-provided correct code but do
     * report bugs (fail) on instructor-provided code that has a deliberate
     * but in the `add()` method:
     * <pre>
     *         , correct.Flist, buggy.Flist#1
     *      add,           2.5,    -5
     *     size,           1.5,     0
     * </pre>
     * The test class would be instantiated first with the class "correct.Flist".
     * If all tests whose names begin with "add" pass, the result would be 2.5/2.5
     * points. If any tests beginning with "add" failed or if there were none,
     * the result would be 0/2.5. The results would be similar (except for
     * maxing out at 1.5) for tests starting with "size".
     * <p>
     * Next, the test class would be instantiated with the class "buggy.Flist"
     * and the integer argument 1 (specified after the hash sign).
     * If any test whose name begins with "add" _fails_, the result would be 5/5.
     * If all tests beginning with "add" passed or if there were none, the
     * result would be 0/5. No results would be produced for test methods
     * with names beginning with "size".
     *
     * @param testClass   the test class to be instantiated with each class under test
     * @param scoringData a source of CSV data with the names of classes under
     *                    test, methods under tests, and scoring information
     */
    public CrossGrader(Class<?> testClass, InputStream scoringData) {
        generalizedTestClass = testClass;
        processCsvFile(scoringData);
    }

    // helper method for initialization
    private void processCsvFile(InputStream is) {
        // Read in file so we know size (number of methods).
        List<String[]> rows = new ArrayList<>();
        try (Scanner scanner = new Scanner(is)) {
            if (scanner.hasNextLine()) {
                String[] fields = scanner.nextLine().split(DELIM);
                cutNames = Arrays.copyOfRange(fields, 1, fields.length);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (!line.isEmpty()) {
                        rows.add(line.split(DELIM));
                    }
                }
            } else {
                throw new RuntimeException("File is empty");
            }
        }

        // Build and populate maxScores 2D-array.
        methodNames = new String[rows.size()];
        points = new double[rows.size()][cutNames.length];
        for (int i = 0; i < methodNames.length; i++) {
            String[] row = rows.get(i);
            if (row.length != cutNames.length + 1) {
                throw new RuntimeException(String.format("Row %d has length %d, not expected length %d",
                        i, row.length, cutNames.length + 1));
            }
            methodNames[i] = row[0];
            for (int j = 1; j < row.length; j++) {
                points[i][j - 1] = Double.parseDouble(row[j]);
            }
        }
    }

    /**
     * Run all the tests as specified in the constructor.
     *
     * @return the results of the tests
     * @throws ClientException if an error occurs due to misconfiguration
     */
    public List<Result> gradeAll() throws ClientException{
        List<Result> results = new ArrayList<>();
        for (int i = 0; i < cutNames.length; i++) {
            results.addAll(grade(i));
        }
        return results;
    }

    private List<Result> grade(int cutIndex) throws ClientException {
        DependencyInjector.reset(); // clear previous injected values
        DependencyInjector.setGeneralizedTestClass(generalizedTestClass);
        final String cutField = cutNames[cutIndex];
        final List<TestResult> testResults = new ArrayList<>();
        String[] cutFields = cutField.split("#");
        try {
            Class<?> classUnderTest = Class.forName(cutFields[0]);
            DependencyInjector.setClassToInject(classUnderTest);
            if (cutFields.length > 1) {
                DependencyInjector.setIntToInject(Integer.parseInt(cutFields[1]));
            }
            Launcher launcher = LauncherFactory.create();
            launcher.registerTestExecutionListeners(new TestExecutionListener() {
                @Override
                public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
                    final String mutName =
                            Arrays.stream(methodNames)
                                    .filter((String name) -> testIdentifier.getDisplayName().startsWith(name))
                                    .findFirst()
                                    .orElse("Method name not found");
                    testResults.add(switch (testExecutionResult.getStatus()) {
                        case SUCCESSFUL -> TestResult.makeSuccess(
                                testIdentifier.getDisplayName(),
                                mutName);
                        case FAILED, ABORTED -> TestResult.makeFailure(
                                testIdentifier.getDisplayName(),
                                mutName,
                                testExecutionResult.getThrowable().isPresent() ?
                                        testExecutionResult.getThrowable().get().getMessage() :
                                        "no information");
                    });
                    TestExecutionListener.super.executionFinished(testIdentifier, testExecutionResult);
                }
            });
            launcher.execute(request().selectors(DiscoverySelectors.selectClass(generalizedTestClass)).build());
            return generateResults(cutIndex, testResults);
        } catch (ClassNotFoundException e) {
            throw new ClientException(
                    String.format(
                            "Crossgrader execution failed because class '%s' specified in the CSV file could not be loaded.",
                            cutFields[0]),
                    e);
        }
    }

    private List<Result> generateResults(int cutIndex, List<TestResult> testResults) {
        List<Result> results = new ArrayList<>(methodNames.length);
        for (int mutIndex = 0; mutIndex < methodNames.length; mutIndex++) {
            // Skip cases where points is 0.
            if (points[mutIndex][cutIndex] != 0) {
                results.add(generateResult(cutIndex, mutIndex, testResults));
            }
        }
        return results;
    }

    private Result generateResult(int cutIndex, int mutIndex, List<TestResult> testResults) {
        String mutName = methodNames[mutIndex];
        List<TestResult> mutTestResults = testResults
                .stream()
                .filter((TestResult tr) ->
                        tr.methodUnderTestName().equals(mutName))
                .toList();
        String name = String.format("Tests of %s.%s()", cutNames[cutIndex], mutName);
        StringBuilder sb = new StringBuilder();
        int successes = 0;
        int failures = 0;
        for (TestResult tr : mutTestResults) {
            if (tr.passed()) {
                sb.append(String.format("Test %s PASSED\n", tr.testName()));
                successes++;
            } else {
                sb.append(String.format("Test %s FAILED: %s", tr.testName(), tr.message()));
                failures++;
            }
        }
        // If maxPoints is positive, full credit is earned for success.
        // If maxPoints is negative, full credit is earned for failure.
        double maxPoints = points[mutIndex][cutIndex];
        double points;
        if (failures > 0) {
            points = maxPoints > 0 ? 0 : -maxPoints;
        } else if (successes > 0) {
            points = maxPoints > 0 ? maxPoints : 0;
        } else {
            points = 0;
            sb.append("No tests found");
        }
        return new Result(
                name,
                points,
                Math.abs(maxPoints),
                sb.toString());
    }
}
