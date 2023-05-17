package newgrader.crossgrader;

import org.junit.jupiter.api.*;

import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.*;

/**
 * A test runner for JUnit5 tests that is specifically designed for:
 * <ul>
 *     <li> tests written by students, with no additional annotation</li>
 *     <li> running against multiple classes under test</li>
 * </ul>
 * <p>
 * The only JUnit annotations that are currently supposed are {@code @Test},
 * {@code @BeforeEach}, and {@code @BeforeAll}.
 *
 * @see CrossGrader
 */
class JUnit5TestRunner {
    private final List<String> methodsUnderTest; // sort longest first

    /**
     * Create a test runner for methods with the given names. This assumes
     * that tests start with the names of the methods under test.
     *
     * @param methodsUnderTest the names of the methods to test
     */
    JUnit5TestRunner(String[] methodsUnderTest) {
        // It is necessary to copy the data so changes to the list
        // don't change the underlying array.
        this.methodsUnderTest = new ArrayList<>(Arrays.asList(methodsUnderTest));
        // Sort longest first to ensure that the correct method under test (MUT)
        // is chosen even if multiple MUTs start with the same substring.
        this.methodsUnderTest.sort(
                (String s1, String s2) -> s2.length() - s1.length()
        );
    }

    @Nullable
    private String getMutName(String testMethodName) {
        for (String mutName : methodsUnderTest) {
            if (testMethodName.startsWith(mutName)) {
                return mutName;
            }
        }
        return null;
    }

    /**
     * Run the tests on the provided instance of the test class, producing
     * a {@link TestResult} for every test that runs. Tests are run only if
     * their names start with the name of one of the methods under test.
     *
     * @param testInstance the test instance
     * @return the results of each test run
     * @throws RuntimeException if there is an {@link IllegalAccessException} or
     *                          an error is through by the {@code @BeforeEach} method
     */
    List<TestResult> runAutograder(Object testInstance) {
        Method[] methods = testInstance.getClass().getMethods();
        List<Method> beforeEachMethods = new ArrayList<>();
        List<TestResult> results = new ArrayList<>();

        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeEach.class)) {
                beforeEachMethods.add(method);
            }
            if (method.isAnnotationPresent(BeforeAll.class)) {
                try {
                    method.invoke(null); // method must be static
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(
                            "Error running @BeforeAll method " + method.getName(),
                            e);
                }
            }
        }
        for (Method method : methods) {
            if (method.isAnnotationPresent(Test.class)) {
                String mutName = getMutName(method.getName());
                try {
                    for (Method beforeEachMethod : beforeEachMethods) {
                        beforeEachMethod.invoke(testInstance);
                    }
                    method.invoke(testInstance);
                    results.add(TestResult.makeSuccess(
                            method.getName(),
                            mutName));
                } catch (IllegalAccessException e) {
                    throw new AssertionError("Internal problem with method " + method.getName());
                } catch (InvocationTargetException e) {
                    System.err.println("Cause: " + e.getCause());
                    results.add(TestResult.makeFailure(
                            method.getName(),
                            mutName,
                            e.getCause().toString()));
                    break;
                }
            }
        }
        return results;
    }
}
