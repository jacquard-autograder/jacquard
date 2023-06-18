package newgrader.common;

import com.github.javaparser.ast.CompilationUnit;
import newgrader.Autograder;

public class PathStringTarget extends Target {
    private final String pathString;

    protected PathStringTarget(String pathString) {
        this.pathString = pathString;
    }

    @Override
    public CompilationUnit toCompilationUnit() {
        return Autograder.parse(toFile());
    }

    @Override
    public String toPathString() {
        return pathString;
    }

    @Override
    public Class<?> toClass() {
        return null;
    }
}
