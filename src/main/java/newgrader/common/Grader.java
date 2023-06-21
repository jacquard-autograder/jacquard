package newgrader.common;

import java.util.*;

public abstract class Grader {
    private final String name;

    public Grader(String name) {
        this.name = name;
    }

    public List<Result> grade(List<Target> targets) {
        List<Result> results = new ArrayList<>();
        for (Target target : targets) {
            results.addAll(grade(target));
        }
        return results;
    }

    public abstract List<Result> grade(Target target);

    protected List<Result> makeSuccessResultList(double maxPoints, String message) {
        return List.of(makeSuccessResult(maxPoints, message));
    }

    protected Result makeSuccessResult(double maxPoints, String message) {
        return Result.makeSuccess(name, maxPoints, message);
    }

    protected List<Result> makeFailureResultList(double maxPoints, String message) {
        return List.of(makeFailureResult(maxPoints, message));
    }

    protected Result makeFailureResult(double maxPoints, String message) {
        return Result.makeTotalFailure(name, maxPoints, message);
    }

    protected List<Result> makeExceptionResultList(Throwable throwable) {
        return List.of(makeExceptionResult(throwable));
    }

    protected Result makeExceptionResult(Throwable throwable) {
        return Result.makeError(name, throwable);
    }

    protected Result makePartialCreditResult(double points, double maxPoints, String message) {
        return Result.makeResult(name, points, maxPoints, message);
    }
}
