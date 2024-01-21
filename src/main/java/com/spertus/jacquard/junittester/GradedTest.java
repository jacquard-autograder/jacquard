package com.spertus.jacquard.junittester;

import com.spertus.jacquard.common.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The GradedTest annotation is primarily based off of capturing the data for
 * a test object in the Gradescope JSON output. They are used to create
 * corresponding {@link Result} objects.
 * <p>
 * This reuses code from <a href="https://github.com/tkutcher/jgrade">jgrade</a>
 * by Tim Kutcher.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GradedTest {
    /**
     * The name of the test. If this is not set, the name of the test method
     * will be used in the corresponding {@link Result}.
     *
     * @return the name of the test
     */
    String name() default "";

    /**
     * A description of the defect detected by this test.
     *
     * @return the description
     */
    String description() default "";

    /**
     * The number of points the test is worth.
     *
     * @return the number of points the test is worth
     */
    double points() default 1.0;

    /**
     * Whether to include the output in the test results.
     *
     * @return {@code true} if the output should be included, {@code false} otherwise
     */
    boolean includeOutput() default true;

    /**
     * The visibility level of the test, which determines when the result is shown to
     * the student whose code is being graded.
     *
     * @return the visibility level of the test
     */
    Visibility visibility() default Visibility.VISIBLE;
}
