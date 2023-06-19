package newgrader.common;

import com.github.javaparser.ast.CompilationUnit;
import newgrader.syntaxgrader.Parser;

public class PathStringTarget extends Target {
    private final String pathString;

    protected PathStringTarget(String pathString) {
        this.pathString = pathString;
    }

    @Override
    public CompilationUnit toCompilationUnit() {
        return Parser.parse(toFile());
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
