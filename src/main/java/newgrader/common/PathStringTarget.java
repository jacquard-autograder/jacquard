package newgrader.common;

import com.github.javaparser.ast.CompilationUnit;
import newgrader.Autograder;

import java.io.File;
import java.nio.file.*;
import java.util.Arrays;
import java.util.regex.Pattern;

public class PathStringTarget extends Target {
    private final String pathString;

    public PathStringTarget(String pathString) {
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
    public File toFile() {
        return new File(pathString);
    }

    @Override
    public Class<?> toClass() {
        return null;
    }
}
