package com.spertus.jacquard.crosstester;

import com.spertus.jacquard.common.Result;
import com.spertus.jacquard.exceptions.ClientException;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A grader for running student-provided tests against multiple implementations.
 * This can be used to test whether student tests fail intentionally buggy
 * code (which is good) or pass correct code (also good).
 */
public class CrossTester {
    private static final String DELIM = "\\s*,\\s*";
    private final Class<?> testClass;
    // The next three instance variables are initialized in processCsvFile().
    private String[] methodNames;
    private String[] putNames; // packages under test
    private double[][] points;

    /**
     * Creates a cross tester that uses the provided test class to instantiate
     * classes under test that are specified in a CSV file.
     * <p>
     * The file should contain the packages under test (in the header row), the
     * methods under test (in the leftmost column), and maximum points for each
     * combination. A positive points value means that points should be scored
     * if the test passes, a negative points value means that points should be
     * scored if the test fails, while a points value of zero means that no
     * points should be scored for the combination. If a points value is
     * non-zero and there are no tests of the specified method, a score of 0 is
     * given.
     * <p>
     * Consider this sample file, which might be used to check whether student
     * tests do not report bugs on instructor-provided correct code but do
     * report bugs (fail) on instructor-provided code that has a deliberate
     * but in the `add()` method:
     * <pre>
     *         , correct, buggy
     *      add,     2.5,  -5
     *     size,     1.5,   0
     * </pre>
     * The test class would be instantiated first with the package "correct".
     * If all tests whose names begin with "add" pass, the result would be 2.5/2.5
     * points. If any tests beginning with "add" failed or if there were none,
     * the result would be 0/2.5. The results would be similar (except for
     * maxing out at 1.5) for tests starting with "size".
     * <p>
     * Next, the test class would be instantiated with the package "buggy". If
     * any test whose name begins with "add" _fails_, the result would be 5/5.
     * If all tests beginning with "add" passed or if there were none, the
     * result would be 0/5. No results would be produced for test methods
     * with names beginning with "size".
     *
     * @param testClass   the class containing the tests
     * @param csvFileName the name of the CSV file, which must be in a resource
     *                    directory
     */
    public CrossTester(final Class<?> testClass, final String csvFileName) {
        this.testClass = testClass;
        InputStream is = getClass().getResourceAsStream("/" + csvFileName);
        processCsvFile(is); // callee closes input stream
    }

    // helper method for initialization, closes input stream
    private void processCsvFile(final InputStream is) {
        // Read in file so we know size (number of methods).
        final List<String[]> rows = new ArrayList<>();
        try (Scanner scanner = new Scanner(is)) {
            if (scanner.hasNextLine()) {
                final String[] fields = scanner.nextLine().split(DELIM);
                putNames = Arrays.copyOfRange(fields, 1, fields.length);
                while (scanner.hasNextLine()) {
                    final String line = scanner.nextLine().trim();
                    if (!line.isEmpty()) {
                        rows.add(line.split(DELIM));
                    }
                }
            } else {
                throw new ClientException("CSV file is empty");
            }
        }

        // Build and populate maxScores 2D-array.
        methodNames = new String[rows.size()];
        points = new double[rows.size()][putNames.length];
        for (int i = 0; i < methodNames.length; i++) {
            final String[] row = rows.get(i);
            if (row.length != putNames.length + 1) {
                throw new ClientException(String.format("Row %d has length %d, not expected length %d",
                        i, row.length, putNames.length + 1));
            }
            methodNames[i] = row[0];
            for (int j = 1; j < row.length; j++) {
                points[i][j - 1] = Double.parseDouble(row[j]);
            }
        }
    }

    /**
     * Runs all the tests as specified in the constructor.
     *
     * @return the results of the tests
     * @throws ClassNotFoundException if a specified package does not contain
     *                                the expected test class
     */
    public List<Result> run() throws ClassNotFoundException {
        final List<TestResult> testResults = new ArrayList<>();

        // Create LauncherDiscoveryRequest.
        LauncherDiscoveryRequestBuilder builder = LauncherDiscoveryRequestBuilder.request();
        for (String putName : putNames) {
            try {
                Class<?> test = Class.forName(putName + "." + testClass.getSimpleName());
                builder.selectors(DiscoverySelectors.selectClass(test));
            } catch (ClassNotFoundException e) {
                throw new ClientException(
                        "Class could not be found, possibly because this was run through the IDE " +
                                "instead of with the proper script.", e);

            }
        }
        LauncherDiscoveryRequest request = builder.build();

        //  Create Launcher.
        final Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(new TestExecutionListener() {
            // TODO: Factor out duplicated code from JUnitTester.
            private PrintStream ps;
            private ByteArrayOutputStream baos;

            @Override
            public void executionStarted(final TestIdentifier testIdentifier) {
                baos = new ByteArrayOutputStream();
                if (ps != null) {
                    ps.close();
                }
                ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
                System.setOut(ps);
                // TODO: Capture System.err.
            }

            @Override
            public void executionFinished(
                    final TestIdentifier testIdentifier,
                    final TestExecutionResult testExecutionResult) {
                if (!testIdentifier.getType().isTest()) {
                    return;
                }
                final String mutName = Arrays
                        .stream(methodNames)
                        .filter(name -> testIdentifier.getDisplayName().startsWith(name))
                        .findFirst()
                        .orElse("Method name not found");
                String putName = "Class name not found"; // likely reinitialized in loop
                for (final UniqueId.Segment segment : testIdentifier.getUniqueIdObject().getSegments()) {
                    if (segment.getType().equals("class")) {
                        String qcn = segment.getValue(); // qualified class name
                        putName = qcn.substring(0, qcn.lastIndexOf('.'));
                        break;
                    }
                }
                final String output = baos.toString().trim();
                testResults.add(switch (testExecutionResult.getStatus()) {
                    case SUCCESSFUL -> TestResult.makeSuccess(
                            testIdentifier.getDisplayName(),
                            mutName,
                            putName,
                            output);
                    case FAILED, ABORTED -> TestResult.makeFailure(
                            testIdentifier.getDisplayName(),
                            mutName,
                            putName,
                            testExecutionResult.getThrowable().isPresent() ?
                                    testExecutionResult.getThrowable().get().getMessage() :
                                    "no information",
                            output);
                });
                TestExecutionListener.super.executionFinished(testIdentifier, testExecutionResult);
            }
        });

        // Prepare to mess with streams.
        final PrintStream originalOut = System.out; // NOPMD
        final PrintStream originalErr = System.err; // NOPMD

        // Run tests.
        launcher.execute(request);

        // Restore streams.
        System.setOut(originalOut);
        System.setErr(originalErr);

        // Generate and return results.
        return generateResults(testResults);
    }

    private List<Result> generateResults(final List<TestResult> testResults) {
        final List<Result> results = new ArrayList<>();
        for (int mutIndex = 0; mutIndex < methodNames.length; mutIndex++) {
            for (int putIndex = 0; putIndex < putNames.length; putIndex++) {
                // Skip cases with 0 points.
                if (points[mutIndex][putIndex] != 0) {
                    results.add(generateResult(putIndex, mutIndex, testResults));
                }
            }
        }
        return results;
    }

    private Result generateResult(final int putIndex, final int mutIndex, final List<TestResult> testResults) {
        // Build list of results for the specified package and method under test
        final String mutName = methodNames[mutIndex];
        final String putName = putNames[putIndex];
        final List<TestResult> mutTestResults = testResults
                .stream()
                .filter((TestResult tr) ->
                        tr.methodUnderTestName().equals(mutName) &&
                                tr.packageUnderTestName().equals(putName))
                .toList();
        final String name = String.format("Submitted tests of %s.%s()", putNames[putIndex], mutName);
        final StringBuilder sb = new StringBuilder();
        int successes = 0;
        int failures = 0;
        for (final TestResult tr : mutTestResults) {
            if (tr.passed()) {
                sb.append(String.format("Test %s PASSED\n", tr.testName()));
                successes++;
            } else {
                sb.append(String.format("Test %s FAILED: %s\n", tr.testName(), tr.message()));
                failures++;
            }
            if (!tr.output().isEmpty()) {
                sb.append('\n');
                sb.append(tr.output());
            }
        }
        // If maxPoints is positive, full credit is earned for success.
        // If maxPoints is negative, full credit is earned for failure.
        final double maxPoints = points[mutIndex][putIndex];
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
