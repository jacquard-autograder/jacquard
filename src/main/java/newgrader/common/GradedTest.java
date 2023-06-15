package newgrader.common;

// This is based on tkutcher/jgrade

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The GradedTest annotation is primarily based off of capturing the data for
 * a test object in the Gradescope JSON output. They are used to create
 * corresponding {@link GradedTestResult} objects.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GradedTest {
    /**
     * The name of the test.
     *
     * @return The name of the test.
     */
    String name() default "Unnamed Test";

    /**
     * The number of points the test is worth.
     *
     * @return The number of points the test is worth.
     */
    double points() default 1.0;

    boolean includeOutput() default true;

    /**
     * The visibility level of the test.
     *
     * @return The visibility level of the test.
     */
    Visibility visibility() default Visibility.VISIBLE;
}
