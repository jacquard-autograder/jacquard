package newgrader.syntaxgrader;

import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.google.common.annotations.VisibleForTesting;
import newgrader.common.Result;
import newgrader.exceptions.ClientException;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Parser {
    private static final ParserConfiguration.LanguageLevel DEFAULT_LANGUAGE_LEVEL =
            ParserConfiguration.LanguageLevel.JAVA_17;
    private final JavaParser parser;

    public Parser(ParserConfiguration.LanguageLevel languageLevel) {
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(languageLevel);
        parser = new JavaParser(config);
    }

    @VisibleForTesting
    public static CompilationUnit parseCode(String program) {
        Parser parser = new Parser(DEFAULT_LANGUAGE_LEVEL);
        ParseResult<CompilationUnit> parseResult = parser.parser.parse(program);
        if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
            return parseResult.getResult().get();
        }
        throw new AssertionError(parseResult.getProblem(0));
    }

    public static CompilationUnit parse(File file) {
        Parser parser = new Parser(DEFAULT_LANGUAGE_LEVEL);
        try {
            ParseResult<CompilationUnit> parseResult = parser.parser.parse(file);
            if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
                return parseResult.getResult().get();
            }
            throw new AssertionError(parseResult.getProblem(0));
        } catch (FileNotFoundException e) {
            throw new ClientException(e.getMessage());
        }
    }
}
