package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.google.common.annotations.VisibleForTesting;
import com.spertus.jacquard.exceptions.ClientException;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A wrapper for {@link JavaParser}.
 */
public class Parser {
    /**
     * The default language level for the parser.
     */
    public static final ParserConfiguration.LanguageLevel DEFAULT_LANGUAGE_LEVEL =
            ParserConfiguration.LanguageLevel.JAVA_17;
    private final JavaParser parser;

    /**
     * Constructs a parser with the default language level.
     */
    public Parser() {
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(DEFAULT_LANGUAGE_LEVEL);
        parser = new JavaParser(config);
    }

    /**
     * Constructs a parser with the given language level.
     *
     * @param languageLevel the language level
     */
    public Parser(ParserConfiguration.LanguageLevel languageLevel) {
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(languageLevel);
        parser = new JavaParser(config);
    }

    private static String joinProblems(List<Problem> problems) {
        return problems.stream().map(Problem::getVerboseMessage).collect(Collectors.joining(" \n"));
    }

    /**
     * Parses a snippet of code. This is provided for testing only.
     *
     * @param program a snippet of code
     * @return the parsed representation
     * @throws ClientException if the code cannot be parsed
     */
    @VisibleForTesting
    public static CompilationUnit parseCode(String program) {
        Parser parser = new Parser(DEFAULT_LANGUAGE_LEVEL);
        ParseResult<CompilationUnit> parseResult = parser.parser.parse(program);
        if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
            return parseResult.getResult().get();
        }
        throw new ClientException(joinProblems(parseResult.getProblems()));
    }

    /**
     * Parses a file.
     *
     * @param file the file
     * @return the parsed representation
     * @throws ClientException if the file cannot be found or cannot be parsed
     */
    public static CompilationUnit parse(File file) {
        Parser parser = new Parser(DEFAULT_LANGUAGE_LEVEL);
        try {
            ParseResult<CompilationUnit> parseResult = parser.parser.parse(file);
            if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
                return parseResult.getResult().get();
            }
            throw new ClientException(joinProblems(parseResult.getProblems()));
        } catch (FileNotFoundException e) {
            throw new ClientException(e.getMessage());
        }
    }
}
