package newgrader.crossgrader;

import newgrader.Result;

import java.io.InputStream;
import java.lang.reflect.*;
import java.util.*;

public class CrossGrader {
    private static final String DELIM = "\\s*,\\s*";
    private final Constructor<?> testClassConstructor;
    // The next four instance variables are initialized in processFile.
    private JUnit5TestRunner runner;
    private String[] methodNames;
    private String[] cutNames; // classes under test
    private double[][] points;

    public CrossGrader(Class<?> testClass, InputStream is) throws NoSuchMethodException {
        testClassConstructor = testClass.getConstructor(String.class);
        processFile(is);
    }

    public List<Result> gradeAll() {
        List<Result> results = new ArrayList<>();
        for (int i = 0; i < cutNames.length; i++) {
            results.addAll(grade(i));
        }
        return results;
    }

    public List<Result> grade(int cutIndex) {
        String cutName = cutNames[cutIndex];
        try {
            Object testInstance = testClassConstructor.newInstance(cutName);
            List<TestResult> testResults = runner.runAutograderHelper(testInstance);
            System.out.println(testResults);
            return generateResults(cutIndex, testResults);
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(
                    "Unable to instantiate class " + cutName,
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
        double maxPoints = points[mutIndex][cutIndex];
        double points = 0;
        if (failures > 0) {
            points = -maxPoints;
        } else if (successes > 0) {
            points = maxPoints;
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

    private void processFile(InputStream is) {
        // Read in file so we know size.
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

        // Create test runner.
        runner = new JUnit5TestRunner(methodNames);
    }
}
