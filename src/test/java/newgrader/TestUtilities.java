package newgrader;

import com.github.javaparser.ast.CompilationUnit;
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
}
