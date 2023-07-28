package com.spertus.jacquard;

import com.spertus.jacquard.common.*;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtilities {
    static Path getPath(String filename) throws URISyntaxException {
        return Paths.get(TestUtilities.class.getClassLoader().getResource(File.separator + filename).toURI());
    }

    static Target getTargetFromResource(String filename) throws URISyntaxException {
        return Target.fromPath(getPath(filename));
    }

    static double getTotalScore(List<Result> results) {
        return results.stream().mapToDouble(Result::getScore).sum();
    }

    static double getTotalMaxScore(List<Result> results) {
        return results.stream().mapToDouble(Result::getMaxScore).sum();
    }

    static void assertResultsMatch(List<Result> results, int expectedSize, double expectedScore, double maxScore) {
        assertEquals(expectedSize, results.size());
        assertEquals(expectedScore, getTotalScore(results));
        assertEquals(maxScore, getTotalMaxScore(results));
    }

    // Makes sure the same result is returned by repeated calls.
    static void testTwice(Grader grader, Target target) {
        List<Result> results1 = grader.grade(target);
        List<Result> results2 = grader.grade(target);
        // Only the last call is required, but earlier ones may give
        // more useful messages.
        assertEquals(results1.size(), results2.size());
        assertEquals(results1, results2);
    }
}
