package newgrader;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * The base class for syntax checkers.
 */
public abstract class Checker implements Processor {
    /**
     * The message that appears in a successful {@link Result} if none is
     * provided.
     */
    public static String SUCCESS_STRING = "SUCCESS";

    /**
     * The name of the checker, if none (or <pre>null</pre>) is provided
     * in the constructor.
     */
    public static String DEFAULT_NAME = "Syntax Checker";

    private final String name;
    protected final double maxScore;
    // The subclass constructor must either pass the adapter to this class's
    // constructor or initialize it before returning.
    protected VoidVisitorAdapter<List<Result>> adapter;

    protected Checker(String name, double maxScore, VoidVisitorAdapter<List<Result>> adapter) {
        this.name = name == null ? DEFAULT_NAME : name;
        this.maxScore = maxScore;
        this.adapter = adapter;
    }

    protected Checker(String name, double maxScore) {
        this.name = name == null ? DEFAULT_NAME : name;
        this.maxScore = maxScore;
    }

    protected Checker(double maxScore, VoidVisitorAdapter<List<Result>> adapter) {
        this(null, maxScore, adapter);
    }

    protected Checker(double maxScore) {
        this(null, maxScore);
    }

    @Override
    public List<Result> process(CompilationUnit cu) {
        List<Result> results = new ArrayList<>();
        adapter.visit(cu, results);
        return results;
    }

    String getName() {
        return name;
    }

    VoidVisitorAdapter<List<Result>> getAdapter() {
        return adapter;
    }

    protected Result makeFailingResult(String message) {
        return new Result(name, 0, maxScore, message);
    }

    protected Result makeSuccessResult(String message) {
        return new Result(name, maxScore, maxScore, message);
    }

    protected Result makeSuccessResult() {
        return makeSuccessResult(SUCCESS_STRING);
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
