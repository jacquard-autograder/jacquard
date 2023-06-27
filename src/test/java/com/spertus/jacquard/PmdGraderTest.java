package com.spertus.jacquard;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.exceptions.ClientException;
import com.spertus.jacquard.pmdgrader.PmdGrader;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PmdGraderTest {
    private static final double PENALTY_PER_VIOLATION = .5;
    private static final double MAX_PENALTY = 2.5;

    private Target missingCommentsTarget;

    @BeforeEach
    public void setup() throws URISyntaxException {
        missingCommentsTarget = TestUtilities.getTargetFromResource("good/MissingComments.java");
    }

    @Test
    public void testSingleRule() throws ClientException {
        PmdGrader pmdGrader = PmdGrader.createFromRules(
                PENALTY_PER_VIOLATION,
                MAX_PENALTY,
                "category/java/documentation.xml",
                "CommentRequired");
        List<Result> results = pmdGrader.grade(missingCommentsTarget);
        assertEquals(1, results.size());
        assertEquals(MAX_PENALTY - 2 * PENALTY_PER_VIOLATION, TestUtilities.getTotalScore(results));
    }

    @Test
    public void testTwoRules() throws ClientException {
        PmdGrader pmdGrader = PmdGrader.createFromRules(
                PENALTY_PER_VIOLATION,
                MAX_PENALTY,
                "category/java/documentation.xml",
                "CommentRequired", "UncommentedEmptyConstructor");
        List<Result> results = pmdGrader.grade(missingCommentsTarget);
        assertEquals(1, results.size());
        assertEquals(MAX_PENALTY - 3 * PENALTY_PER_VIOLATION, TestUtilities.getTotalScore(results));
    }

    @Test
    public void testBadRuleset() {
        assertThrows(ClientException.class,
                () -> PmdGrader.createFromRuleSetPaths(
                        PENALTY_PER_VIOLATION,
                        MAX_PENALTY,
                        "category/java/documentation.xml",
                        "BADPATH/java/documentation.xml"));
        assertThrows(ClientException.class,
                () -> PmdGrader.createFromRules(
                        PENALTY_PER_VIOLATION,
                        MAX_PENALTY,
                        "XXcategory/java/documentation.xml",
                        "CommentRequired", "UncommentedEmptyConstructor"));
    }

    @Test
    public void testBadRulename() {
        assertThrows(ClientException.class,
                () -> PmdGrader.createFromRules(
                        PENALTY_PER_VIOLATION,
                        MAX_PENALTY,
                        "category/java/documentation.xml",
                        "CommentRequired", "NoSuchRule"));
    }

    @Test
    public void testSingleRuleSet() throws ClientException {
        PmdGrader pmdGrader = PmdGrader.createFromRuleSetPaths(
                PENALTY_PER_VIOLATION,
                MAX_PENALTY,
                "category/java/documentation.xml");
        List<Result> results = pmdGrader.grade(missingCommentsTarget);
        assertEquals(1, results.size());
        assertEquals(MAX_PENALTY - 3 * PENALTY_PER_VIOLATION, TestUtilities.getTotalScore(results));
    }

    @Test
    public void testTwoRuleSets() throws ClientException {
        PmdGrader pmdGrader = PmdGrader.createFromRuleSetPaths(
                PENALTY_PER_VIOLATION,
                MAX_PENALTY,
                "category/java/documentation.xml",
                "category/java/codestyle.xml");
        List<Result> results = pmdGrader.grade(missingCommentsTarget);
        assertEquals(1, results.size());
        assertEquals(MAX_PENALTY - 5 * PENALTY_PER_VIOLATION, TestUtilities.getTotalScore(results));
    }

    @Test
    public void testDirectory() throws URISyntaxException {
        PmdGrader pmdGrader = PmdGrader.createFromRuleSetPaths(
                PENALTY_PER_VIOLATION,
                MAX_PENALTY,
                "category/java/documentation.xml",
                "category/java/codestyle.xml");
        Target target = Target.fromPathString("src/test/resources/good/");
        List<Result> results = pmdGrader.grade(target);
        assertEquals(1, results.size());
        assertEquals(0, results.get(0).score()); // lots of errors
    }
}