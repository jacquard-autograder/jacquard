package newgrader;

import com.github.javaparser.ast.CompilationUnit;
import newgrader.common.Result;

import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.List;

public class TestUtilities {
    private static final String CODE_TEMPLATE = """
            public class Main {
                public static void main(String[] args) {
                %s
                }
            }
            """;

    static CompilationUnit parseProgramFromStatements(String statements) {
        return Autograder.parse(String.format(CODE_TEMPLATE, statements));
    }

    static CompilationUnit parseProgramFromClass(String statements) {
        return Autograder.parse(statements);
    }

    static double getTotalScore(List<Result> results) {
        return results.stream().mapToDouble(Result::score).sum();
    }

    static Path getPath(String filename) throws URISyntaxException {
        return Paths.get(TestUtilities.class.getClassLoader().getResource(filename).toURI());
    }
}
