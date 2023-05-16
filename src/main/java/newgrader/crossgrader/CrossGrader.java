package newgrader.crossgrader;

import client.staff.Flist;
import client.staff.FlistTest;
import client.staff.GeneralizedFlistTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CrossGrader {
    private static final String DELIM = "\\s*,\\s*";
    private final Class<?> testClass;
    private String[] methodNames;
    private String[] cutNames; // classes under test
    private double[][] maxScores;

    public CrossGrader(Class<?> testClass, InputStream is) throws FileNotFoundException {
        this.testClass = testClass;
        processFile(is);
    }

    public void grade() {
        JUnit5TestRunner runner = new JUnit5TestRunner(methodNames);
        for (String cutName : cutNames) {
            try {
                Class<? extends Flist<?>> clazz = (Class<? extends Flist<?>>) Class.forName(cutName);
                Object testInstance = testClass.getConstructors()[0].newInstance(clazz);
                Set<JUnit5TestRunner.TestFailureInfo> failures = new HashSet<>();
                runner.runAutograderHelper(testInstance, failures);
                System.out.println(failures);
            } catch (ClassNotFoundException |
                     InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
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
