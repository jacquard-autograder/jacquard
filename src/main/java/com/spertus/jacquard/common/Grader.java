package com.spertus.jacquard.common;

import com.spertus.jacquard.exceptions.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * The superclass of all graders.
 */
public abstract class Grader {
    private static final long TIMEOUT_MS = 10000;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    private final String name;

    /**
     * Creates a grader.
     *
     * @param name the name of the grader
     */
    public Grader(String name) {
        this.name = name;
    }

    /**
     * Grades the specified target files and directories.
     *
     * @param targets the targets
     * @return the results
     */
    public List<Result> grade(List<Target> targets) {
        List<Result> results = new ArrayList<>();
        for (Target target : targets) {
            results.addAll(grade(target));
        }
        return results;
    }

    /**
     * Grades the provided targets.
     *
     * @param targets the targets
     * @return the results
     */
    public List<Result> grade(Target... targets) {
        List<Result> results = new ArrayList<>();
        try {
            for (Target target : targets) {
                Future<List<Result>> future = executor.submit(getCallable(target));
                results.addAll(future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS));
            }
        } catch (TimeoutException e) {
            results.add(makeExceptionResult(
                    new ClientException("Operation timed out")));
        } catch (InterruptedException | ExecutionException e) {
            // This currently returns after the first exception is thrown,
            // rather than continuing to other targets.
            results.add(
                    makeExceptionResult(
                            new InternalException(
                                    "Internal error", e.getCause())));
        }
        return results;
    }

    public abstract Callable<List<Result>> getCallable(Target target);

    /**
     * Creates a one-element list holding a result indicating complete success.
     *
     * @param maxPoints the maximum number of points, all of which are earned
     * @param message   any message to include
     * @return the result
     */
    protected List<Result> makeSuccessResultList(double maxPoints, String message) {
        return List.of(makeSuccessResult(maxPoints, message));
    }

    /**
     * Creates a result for a fully successful outcome.
     *
     * @param maxPoints the maximum number of points, all of which are awarded
     * @param message   any message to include
     * @return the result
     */
    protected Result makeSuccessResult(double maxPoints, String message) {
        return Result.makeSuccess(name, maxPoints, message);
    }

    /**
     * Creates a one-element list holding the result of a completely
     * unsuccessful outcome.
     *
     * @param maxPoints the maximum number of points, none of which are awarded
     * @param message   any message to include
     * @return the result
     */
    protected List<Result> makeFailureResultList(double maxPoints, String message) {
        return List.of(makeFailureResult(maxPoints, message));
    }

    /**
     * Creates a result for a completely unsuccessful outcome.
     *
     * @param maxPoints the maximum number of points, none of which are earned
     * @param message   any message to include
     * @return the result
     */
    protected Result makeFailureResult(double maxPoints, String message) {
        return Result.makeTotalFailure(name, maxPoints, message);
    }

    /**
     * Creates a one-element list holding the result for a single unhandled
     * throwable.
     *
     * @param throwable the unhandled throwable
     * @return the result
     */
    protected List<Result> makeExceptionResultList(Throwable throwable) {
        return List.of(makeExceptionResult(throwable));
    }

    /**
     * Creates a result when a grader threw an exception instead of returning
     * a result.
     *
     * @param throwable the unhandled throwable
     * @return the result
     */
    protected Result makeExceptionResult(Throwable throwable) {
        return Result.makeError(name, throwable);
    }

    /**
     * Creates a result indicating partial credit.
     *
     * @param points    the number of points awarded
     * @param maxPoints the maximum possible points
     * @param message   any message
     * @return the result
     */
    protected Result makePartialCreditResult(double points, double maxPoints, String message) {
        return Result.makeResult(name, points, maxPoints, message);
    }
}
