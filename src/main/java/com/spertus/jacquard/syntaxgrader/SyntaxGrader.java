package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.CompilationUnit;
import com.spertus.jacquard.common.*;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * The base class for syntax-based graders that make use of the linked
 * <a href="https://javaparser.org/">Java parser</a>.
 */
public abstract class SyntaxGrader extends Grader {
    /**
     * Constructs a syntax-based grader.
     *
     * @param name the name
     */
    public SyntaxGrader(String name) {
        super(name);
    }

    @Override
    public Callable<List<Result>> getCallable(final Target target) {
        final Parser parser = new Parser();
        return () -> grade(parser.parse(target.toFile()));
    }

    /**
     * Grades the parsed compilation unit.
     *
     * @param cu the parsed compilation unit
     * @return the results
     */
    protected abstract List<Result> grade(CompilationUnit cu);

}
