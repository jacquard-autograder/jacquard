package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.spertus.jacquard.common.Autograder;
import com.spertus.jacquard.exceptions.*;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A wrapper for {@link JavaParser}.
 */
public class Parser {
    /**
     * The minimum level of Java supported through this class.
     */
    public static final int MIN_JAVA_LEVEL = 8;

    /**
     * The maximum level of Java currently supported through this class.
     * Preview versions are not supported.
     */
    public static final int MAX_JAVA_LEVEL = 17;

    private static final ParserConfiguration.LanguageLevel[] LEVELS =
            {
                    ParserConfiguration.LanguageLevel.JAVA_8,
                    ParserConfiguration.LanguageLevel.JAVA_9,
                    ParserConfiguration.LanguageLevel.JAVA_10,
                    ParserConfiguration.LanguageLevel.JAVA_11,
                    ParserConfiguration.LanguageLevel.JAVA_12,
                    ParserConfiguration.LanguageLevel.JAVA_13,
                    ParserConfiguration.LanguageLevel.JAVA_14,
                    ParserConfiguration.LanguageLevel.JAVA_15,
                    ParserConfiguration.LanguageLevel.JAVA_16,
                    ParserConfiguration.LanguageLevel.JAVA_17,
            };

    private final JavaParser javaParser;

    /**
     * Constructs a parser.
     */
    public Parser() throws ClientException {
        final int javaLevel = Autograder.getInstance().javaLevel;
        if (javaLevel < MIN_JAVA_LEVEL || javaLevel > MAX_JAVA_LEVEL) {
            throw new ClientException(
                    String.format("SyntaxGrader cannot be used with language level %d, only (%d-%d)",
                            javaLevel, MIN_JAVA_LEVEL, MAX_JAVA_LEVEL));
        }
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(LEVELS[javaLevel - MIN_JAVA_LEVEL]);
        javaParser = new JavaParser(config);
    }

    private static String joinProblems(List<Problem> problems) {
        return problems.stream().map(Problem::getVerboseMessage).collect(Collectors.joining(" \n"));
    }

    /**
     * Parses a file.
     *
     * @param file the file
     * @return the parsed representation
     * @throws SubmissionException if the file cannot be found or cannot be parsed
     */
    public CompilationUnit parse(File file) throws SubmissionException {
        try {
            ParseResult<CompilationUnit> parseResult = javaParser.parse(file);
            if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
                return parseResult.getResult().get();
            }
            throw new SubmissionException(
                    "Unable to parse " + file + ":\n" + joinProblems(parseResult.getProblems()));
        } catch (FileNotFoundException e) {
            throw new SubmissionException("Unable to find file " + file);
        }
    }
}
