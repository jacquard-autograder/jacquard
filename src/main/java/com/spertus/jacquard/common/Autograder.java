package com.spertus.jacquard.common;

import com.spertus.jacquard.exceptions.*;
import com.spertus.jacquard.exceptions.TimeoutException;

import java.util.*;
import java.util.concurrent.*;

public class Autograder {
    /**
     * The default timeout for a {@link Grader}, in milliseconds.
     */
    public static final long DEFAULT_TIMEOUT_MS = 10000L;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private long timeoutMilliseconds = DEFAULT_TIMEOUT_MS;

    /**
     * Creates an autograder.
     */
    public Autograder() {
    }

    /**
     * Sets the timeout for {@link Grader} execution. If this method
     * is not called, {@link #DEFAULT_TIMEOUT_MS} is used.
     *
     * @param timeout the timeout in milliseconds.
     */
    public void setTimeout(long timeout) {
        timeoutMilliseconds = timeout;
    }

    /**
     * Grades the specified target files and directories.
     *
     * @param targets the targets
     * @return the results
     */
    public List<Result> grade(Grader grader, List<Target> targets) {
        List<Result> results = new ArrayList<>();
        for (Target target : targets) {
            results.addAll(grade(grader, target));
        }
        return results;
    }

    /**
     * Grades the provided targets.
     *
     * @param targets the targets
     * @return the results
     */
    public List<Result> grade(Grader grader, Target... targets) {
        List<Result> results = new ArrayList<>();
        try {
            for (Target target : targets) {
                Future<List<Result>> future = executor.submit(grader.getCallable(target));
                results.addAll(future.get(timeoutMilliseconds, TimeUnit.MILLISECONDS));
            }
        } catch (java.util.concurrent.TimeoutException e) {
            results.add(grader.makeExceptionResult(
                    new TimeoutException("Operation timed out")));
        } catch (InterruptedException | ExecutionException e) {
            // This currently returns after the first exception is thrown,
            // rather than continuing to other targets.
            results.add(
                    grader.makeExceptionResult(
                            new InternalException(
                                    "Internal error", e.getCause())));
        }
        return results;
    }
}
