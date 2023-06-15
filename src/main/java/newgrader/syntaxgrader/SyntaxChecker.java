package newgrader.syntaxgrader;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Preconditions;
import newgrader.common.Result;

import java.util.*;

/**
 * The base class for any parser-based checkers.
 */
public abstract class SyntaxChecker implements SyntaxGrader {
    /**
     * The message that appears in a successful {@link Result} if none is
     * provided.
     */
    public static final String SUCCESS_STRING = "SUCCESS";

    /**
     * The name of the checker, if none (or <pre>null</pre>) is provided
     * in the constructor.
     */
    public static final String DEFAULT_NAME = "Syntax Checker";

    private final String name;
    protected final double maxScorePerInstance;
    protected VoidVisitorAdapter<List<Result>> adapter;

    /**
     * Constructs a syntax checker. If the adapter is null, the constructor in
     * the concrete subclass must set it before returning. (Non-static adapters
     * that are inner classes cannot be created until this constructor has
     * completed.)
     *
     * @param name                the name of the syntax checker
     * @param maxScorePerInstance the maximum score per application
     * @param adapter             the adapter
     */
    protected SyntaxChecker(String name, double maxScorePerInstance, VoidVisitorAdapter<List<Result>> adapter) {
        this.name = name == null ? DEFAULT_NAME : name;
        this.maxScorePerInstance = maxScorePerInstance;
        this.adapter = adapter;
    }

    /**
     * Performs any setup before a call to {@link #grade(CompilationUnit)}.
     */
    public void initialize() {
    }

    /**
     * Adds any results that cannot be computed until all visits are complete.
     *
     * @param results the list of results, which may be mutated by this call
     */
    public void finalizeResults(List<Result> results) {
    }

    @Override
    public List<Result> grade(CompilationUnit cu) {
        initialize();
        final List<Result> results = new ArrayList<>();
        adapter.visit(cu, results);
        finalizeResults(results);
        return results;
    }

    protected Result makeFailingResult(String message) {
        return new Result(name, 0, maxScorePerInstance, message);
    }

    protected Result makeSuccessResult(String message) {
        return new Result(name, maxScorePerInstance, maxScorePerInstance, message);
    }

    protected Result makeSuccessResult() {
        return new Result(name, maxScorePerInstance, maxScorePerInstance, SUCCESS_STRING);
    }

    // The remaining methods are helper methods for subclasses.
    protected String getEnclosingClassName(FieldDeclaration fd) {
        if (fd.getParentNode().isPresent() &&
                fd.getParentNode().get() instanceof ClassOrInterfaceDeclaration classOrInterface) {
            return classOrInterface.getNameAsString();
        } else {
            return "CLASS UNKNOWN";
        }
    }

    protected boolean isField(VariableDeclarator vd) {
        return vd.getParentNode().isPresent()
                && vd.getParentNode().get() instanceof FieldDeclaration;
    }

    // This should be called only if isField() is true.
    protected FieldDeclaration getFieldDeclaration(VariableDeclarator vd) {
        Preconditions.checkState(isField(vd));
        // The precondition guarantees the safety of the get() and cast.
        return (FieldDeclaration) vd.getParentNode().get();
    }

    protected String getDeclarationDescription(FieldDeclaration fd, VariableDeclarator vd) {
        return String.format(
                "Declaration of %s variable %s in class %s",
                fd.getModifiers().contains(Modifier.staticModifier()) ? "static" : "instance",
                vd.getNameAsString(),
                getEnclosingClassName(fd));
    }
}
