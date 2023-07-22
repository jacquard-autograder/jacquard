package com.spertus.jacquard;

import com.github.javaparser.ast.CompilationUnit;
import com.spertus.jacquard.common.*;
import com.spertus.jacquard.syntaxgrader.Parser;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.List;

public class TestUtilities {
    static double getTotalScore(List<Result> results) {
        return results.stream().mapToDouble(Result::getScore).sum();
    }

    static Path getPath(String filename) throws URISyntaxException {
        return Paths.get(TestUtilities.class.getClassLoader().getResource(File.separator + filename).toURI());
    }

    static Target getTargetFromResource(String filename) throws URISyntaxException {
        return Target.fromPath(getPath(filename));
    }
}
