package com.spertus.jacquard.common;

import com.spertus.jacquard.exceptions.*;
import com.spertus.jacquard.exceptions.TimeoutException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * The superclass of all graders.
 */
@SuppressWarnings("PMD.TooManyMethods")
public abstract class Grader {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final String name;

    /**
     * Creates a grader.
     *
     * @param name the name of the grader
     * @throws ClientException if {@link Autograder} has not been initialized
     */
    public Grader(final String name) {
        if (!Autograder.isInitialized()) {
            throw new ClientException("Autograder must be initialized before creating a grader.");
        }
        this.name = name;
    }

    /**
     * Gets a {@link Callable} through which this grader can be called on
     * a single target.
     *
     * @param target the target
     * @return a {@link Callable} through which this grader can be called
     */
    public abstract Callable<List<Result>> getCallableSingleTarget(Target target);

    /**
     * Gets a {@link Callable} through which this grader can be called on
     * multiple targets. The default implementation just appends the results
     * of successive calls to {@link #getCallableSingleTarget(Target)}, but
     * subclasses may provide their own implementations to handle multiple
     * targets specially.
     *
     * @param targets the targets
     * @return a {@link Callable} through which this grader can be called
     */
    public Callable<List<Result>> getCallableMultiTarget(Target... targets) {
        final List<Callable<List<Result>>> callables = Arrays.stream(targets)
                .map(target -> getCallableSingleTarget(target))
                .collect(Collectors.toList());
        ;
        final List<Result> results = new ArrayList<>();

        return () -> {
            for (Callable<List<Result>> callable : callables) {
                results.addAll(callable.call());
            }
            return results;
        };
    }

    private List<Result> gradeUntimed(final Target... targets) {
        final List<Result> results = new ArrayList<>();
        try {
            if (targets.length == 1) {
                results.addAll(getCallableSingleTarget(targets[0]).call());
            } else if (targets.length > 1) {
                results.addAll(getCallableMultiTarget(targets).call());
            }
        } catch (Exception e) { // NOPMD
            results.add(makeExceptionResult(new InternalException(e)));
        }
        return results;
    }

    private List<Result> gradeTimed(final Target... targets) {
        final List<Result> results = new ArrayList<>();
        try {
            final Future<List<Result>> future = executor.submit(
                    targets.length == 1 ? getCallableMultiTarget(targets[0])
                            : getCallableMultiTarget(targets));
            results.addAll(future.get(Autograder.getInstance().timeoutMillis, TimeUnit.MILLISECONDS));
        } catch (java.util.concurrent.TimeoutException e) {
            results.add(makeExceptionResult(
                    new TimeoutException("Operation timed out")));
        } catch (InterruptedException | ExecutionException e) {
            // This currently returns after the first exception is thrown,
            // rather than continuing to other targets.
            results.add(makeExceptionResult(e.getCause() == null ? e : e.getCause()));
        }
        return results;
    }

    /**
     * Grades the provided targets.
     *
     * @param targets the targets
     * @return the results
     * @throws ClientException if {@link Autograder} has not been initialized
     */
    public List<Result> grade(final List<Target> targets) {
        return grade(targets.toArray(new Target[0]));
    }

    /**
     * Grades the provided targets.
     *
     * @param targets the targets
     * @return the results
     * @throws ClientException if {@link Autograder} has not been initialized
     */
    public List<Result> grade(final Target... targets) {
        if (Autograder.getInstance().timeoutMillis == 0) {
            return gradeUntimed(targets);
        } else {
            return gradeTimed(targets);
        }
    }

    /**
     * Runs all graders on the provided target.
     *
     * @param target  the target for each of the graders
     * @param graders graders, in the order they should be run
     * @return the collected results
     * @throws ClientException if {@link Autograder} has not been initialized
     */
    public static List<Result> gradeAll(final Target target, final Grader... graders) {
        final List<Result> results = new ArrayList<>();
        for (final Grader grader : graders) {
            final List<Result> r = grader.grade(target);
            results.addAll(r);
        }
        return results;
    }

    /**
     * Creates a one-element list holding a result indicating complete success.
     *
     * @param maxPoints the maximum number of points, all of which are earned
     * @param message   any message to include
     * @return the result
     */
    protected List<Result> makeSuccessResultList(
            final double maxPoints,
            final String message) {
        return List.of(makeSuccessResult(maxPoints, message));
    }

    /**
     * Creates a result for a fully successful outcome.
     *
     * @param maxPoints the maximum number of points, all of which are awarded
     * @param message   any message to include
     * @return the result
     */
    protected Result makeSuccessResult(final double maxPoints, final String message) {
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
    protected List<Result> makeFailureResultList(
            final double maxPoints,
            final String message) {
        return List.of(makeFailureResult(maxPoints, message));
    }

    /**
     * Creates a result for a completely unsuccessful outcome.
     *
     * @param maxPoints the maximum number of points, none of which are earned
     * @param message   any message to include
     * @return the result
     */
    protected Result makeFailureResult(final double maxPoints, final String message) {
        return Result.makeFailure(name, maxPoints, message);
    }

    /**
     * Creates a one-element list holding the result for a single unhandled
     * throwable.
     *
     * @param throwable the unhandled throwable
     * @return the result
     */
    protected List<Result> makeExceptionResultList(final Throwable throwable) {
        return List.of(makeExceptionResult(throwable));
    }

    /**
     * Creates a result when a grader threw an exception instead of returning
     * a result.
     *
     * @param throwable the unhandled throwable
     * @return the result
     */
    protected Result makeExceptionResult(final Throwable throwable) {
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
    protected Result makePartialCreditResult(
            final double points,
            final double maxPoints,
            final String message) {
        return Result.makeResult(name, points, maxPoints, message);
    }
}
