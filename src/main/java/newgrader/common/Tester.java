package newgrader.common;

import java.util.List;

/**
 * The superclass for JUnit-based testers.
 */
public abstract class Tester {
    private final String name;

    /**
     * Creates a tester.
     *
     * @param name the name of the tester
     */
    protected Tester(String name) {
        this.name = name;
    }

    /**
     * Runs the tests.
     *
     * @return the results
     */
    public abstract List<Result> run();
}
