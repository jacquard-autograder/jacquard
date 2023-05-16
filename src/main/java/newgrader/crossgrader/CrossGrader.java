package newgrader.crossgrader;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class CrossGrader {
    private static final String DELIM = "\\s*,\\s*";
    private final Class<?> testClass;
    private final Constructor<?> testClassConstructor;
    private String[] methodNames;
    private String[] cutNames;  // classes under test
    private double[][] maxScores;

    public CrossGrader(Class<?> testClass, InputStream is) throws FileNotFoundException, NoSuchMethodException {
        this.testClass = testClass;
        testClassConstructor = testClass.getConstructor(String.class);
        processFile(is);
    }

    public void grade() {
        JUnit5TestRunner runner = new JUnit5TestRunner(methodNames);
        for (String cutName : cutNames) {
            try {
                Object testInstance = testClassConstructor.newInstance(cutName);
                List<JUnit5TestRunner.TestResult> results = runner.runAutograderHelper(testInstance);
                System.out.println(results);
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(
                        "Unable to instantiate class " + cutName,
                        e);
            }
        }
    }

    private void processFile(InputStream is) throws FileNotFoundException {
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
        maxScores = new double[rows.size()][cutNames.length];
        for (int i = 0; i < methodNames.length; i++) {
            String[] row = rows.get(i);
            if (row.length != cutNames.length + 1) {
                throw new RuntimeException(String.format("Row %d has length %d, not expected length %d",
                        i, row.length, cutNames.length + 1));
            }
            methodNames[i] = row[0];
            for (int j = 1; j < row.length; j++) {
                maxScores[i][j - 1] = Double.parseDouble(row[j]);
            }

        }
    }
}
