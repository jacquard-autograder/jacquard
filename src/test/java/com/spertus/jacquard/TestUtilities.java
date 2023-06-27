package com.spertus.jacquard;

import com.github.javaparser.ast.CompilationUnit;
import com.spertus.jacquard.common.*;
import com.spertus.jacquard.syntaxgrader.Parser;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.List;

public class TestUtilities {
    private static final String CODE_TEMPLATE = """
            public class MyClass {
                public static void foo(String[] args) {
                %s
                }
            }
            """;

    static CompilationUnit parseProgramFromStatements(String statements) {
        return Parser.parseCode(String.format(CODE_TEMPLATE, statements));
    }

    static CompilationUnit parseProgramFromClass(String statements) {
        return Parser.parseCode(statements);
    }

    static double getTotalScore(List<Result> results) {
        return results.stream().mapToDouble(Result::score).sum();
    }

    static Path getPath(String filename) throws URISyntaxException {
        return Paths.get(TestUtilities.class.getClassLoader().getResource(File.separator + filename).toURI());
    }

    static Target getTargetFromResource(String filename) throws URISyntaxException {
        return Target.fromPath(getPath(filename));
    }
}
