package com.spertus.jacquard.common;

import java.util.List;

/**
 * The superclass for JUnit-based testers.
 */
public abstract class Tester {
    /**
     * Creates a tester.
     */
    protected Tester() {
    }

    /**
     * Runs the tests.
     *
     * @return the results
     */
    public abstract List<Result> run();
}
