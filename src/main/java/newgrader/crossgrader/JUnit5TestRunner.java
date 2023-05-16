package newgrader.crossgrader;

import org.junit.jupiter.api.*;

import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.*;

public class JUnit5TestRunner {
    private List<String> methodsUnderTest; // sort longest first

    public JUnit5TestRunner(String[] methodsUnderTest) {
        this.methodsUnderTest = Arrays.asList(methodsUnderTest);
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

    public List<TestResult> runAutograderHelper(Object testInstance) {
        Method[] methods = testInstance.getClass().getMethods();
        List<Method> beforeEachMethods = new ArrayList<>();
        List<TestResult> results = new ArrayList<>();

        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeEach.class)) {
                beforeEachMethods.add(method);
            }
            if (method.isAnnotationPresent(BeforeAll.class)) {
                try {
                    method.invoke(testInstance); // should this be null?
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new AssertionError("Error running @BeforeAll method " + method.getName());
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

    public record TestResult(String testName, boolean passed, String methodUnderTestName, String message) {
        private static final String SUCCESS_MESSAGE = "PASSED";

        private static TestResult makeFailure(String testName, String methodUnderTestName, String message) {
            return new TestResult(testName, false, methodUnderTestName, message);
        }

        private static TestResult makeSuccess(String testName, String methodUnderTestName) {
            return new TestResult(testName, true, methodUnderTestName, SUCCESS_MESSAGE);
        }
    }
}
