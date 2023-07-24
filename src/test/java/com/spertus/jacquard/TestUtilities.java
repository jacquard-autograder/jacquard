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
}
