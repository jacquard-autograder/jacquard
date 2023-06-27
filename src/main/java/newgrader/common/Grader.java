package newgrader.common;

import java.util.*;

/**
 * The superclass of all graders.
 */
public abstract class Grader {
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
     * Grades a single target.
     *
     * @param target the target
     * @return the results
     */
    public abstract List<Result> grade(Target target);

    /**
     * Creates a one-element list holding a result indicating complete success.
     *
     * @param maxPoints the maximum number of points, all of which are earned
     * @param message any message to include
     * @return the result
     */
    protected List<Result> makeSuccessResultList(double maxPoints, String message) {
        return List.of(makeSuccessResult(maxPoints, message));
    }

    /**
     * Creates a result for a fully successful outcome.
     *
     * @param maxPoints the maximum number of points, all of which are awarded
     * @param message any message to include
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
     * @param message any message to include
     * @return the result
     */
    protected List<Result> makeFailureResultList(double maxPoints, String message) {
        return List.of(makeFailureResult(maxPoints, message));
    }

    /**
     * Creates a result for a completely unsuccessful outcome.
     *
     * @param maxPoints the maximum number of points, none of which are earned
     * @param message any message to include
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
     * @param points the number of points awarded
     * @param maxPoints the maximum possible points
     * @param message any message
     * @return the result
     */
    protected Result makePartialCreditResult(double points, double maxPoints, String message) {
        return Result.makeResult(name, points, maxPoints, message);
    }
}
