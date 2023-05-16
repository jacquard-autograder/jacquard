package newgrader;

import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.google.common.annotations.VisibleForTesting;
import newgrader.syntaxgrader.SyntaxGrader;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Autograder {
    private static final ParserConfiguration.LanguageLevel DEFAULT_LANGUAGE_LEVEL =
            ParserConfiguration.LanguageLevel.JAVA_17;
    private final JavaParser parser;
    private final List<SyntaxGrader> processors = new ArrayList<>();
    // classes or interfaces that the student is expected to subtype
    private final List<Class<?>> supertypes = new ArrayList<>();
    private double maxScore = 0.0;

    public Autograder(ParserConfiguration.LanguageLevel languageLevel) {
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(languageLevel);
        parser = new JavaParser(config);
    }

    @VisibleForTesting
    public static CompilationUnit parse(String program) {
        Autograder autograder = new Autograder(DEFAULT_LANGUAGE_LEVEL);
        ParseResult<CompilationUnit> parseResult = autograder.parser.parse(program);
        if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
            return parseResult.getResult().get();
        }
        throw new AssertionError(parseResult.getProblem(0));
    }

    public void addProcessor(SyntaxGrader processor) {
        processors.add(processor);
        maxScore += processor.getTotalMaxScore();
    }

    public void addSupertype(Class<?> clazz) {
        supertypes.add(clazz);
    }

    public List<Result> grade(File file) throws FileNotFoundException {
        ParseResult<CompilationUnit> parseResult = parser.parse(file);
        return process(parseResult);
    }

    public List<Result> grade(InputStream is) {
        ParseResult<CompilationUnit> parseResult = parser.parse(is);
        return process(parseResult);
    }

    public List<Result> grade(Path path) {
        try {
            ParseResult<CompilationUnit> parseResult = parser.parse(path);
            return process(parseResult);
        } catch (IOException e) {
            return List.of(Result.makeFailure("Parse Error", maxScore, e.getMessage()));
        }
    }

    public List<Result> grade(Reader reader) {
        ParseResult<CompilationUnit> parseResult = parser.parse(reader);
        return process(parseResult);
    }

    public List<Result> grade(String s) {
        ParseResult<CompilationUnit> parseResult = parser.parse(s);
        return process(parseResult);
    }

    private List<Result> process(ParseResult<CompilationUnit> parseResult) {
        if (parseResult.isSuccessful()) {
            CompilationUnit cu = parseResult.getResult().get();
            List<Result> results = new ArrayList<>();
            for (SyntaxGrader processor : processors) {
                results.addAll(processor.grade(cu));
            }
            return results;
        } else {
            return List.of(Result.makeFailure("Fatal error", maxScore, "Internal error processing code"));
        }
    }
}
