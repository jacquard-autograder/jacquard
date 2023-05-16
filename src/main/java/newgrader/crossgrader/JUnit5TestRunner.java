package newgrader.crossgrader;

import org.junit.jupiter.api.*;

import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.*;

public class JUnit5TestRunner {
    private List<String> methodsUnderTest; // sort longest first

    public JUnit5TestRunner(String[] methodsUnderTest) {
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
                    method.invoke(null); // method must be static
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

}
