package newgrader;

import com.github.javaparser.ast.CompilationUnit;

public class TestUtilities {
    private static final String CODE_TEMPLATE = """
            public class Main {
                public static void main(String[] args) {
                %s
                }
            }
            """;

    static String makeProgram(String statements) {
        return String.format(CODE_TEMPLATE, statements);
    }

    static CompilationUnit parse(String statements) {
        String program = makeProgram(statements);
        return Autograder.parse(program);
    }
}
