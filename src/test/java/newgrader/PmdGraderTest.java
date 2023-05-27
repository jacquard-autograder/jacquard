package newgrader;

import newgrader.exceptions.ClientException;
import newgrader.pmdgrader.PmdGrader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PmdGraderTest {
    private static double PENALTY_PER_VIOLATION = .5;
    private static double MAX_PENALTY = 2.5;

    @Test
    public void testSingleRule() throws IOException, URISyntaxException, ClientException {
        PmdGrader pmdGrader = PmdGrader.createFromRules(
                PENALTY_PER_VIOLATION,
                MAX_PENALTY,
                "category/java/documentation.xml",
                "CommentRequired");
        List<Result> results = pmdGrader.grade(TestUtilities.getPath("MissingComments.java"));
        assertEquals(1, results.size());
        assertEquals(MAX_PENALTY - 2 * PENALTY_PER_VIOLATION, TestUtilities.getTotalScore(results));
    }

    @Test
    public void testTwoRules() throws IOException, URISyntaxException, ClientException {
        PmdGrader pmdGrader = PmdGrader.createFromRules(
                PENALTY_PER_VIOLATION,
                MAX_PENALTY,
                "category/java/documentation.xml",
                "CommentRequired", "UncommentedEmptyConstructor");
        List<Result> results = pmdGrader.grade(TestUtilities.getPath("MissingComments.java"));
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
    public void testSingleRuleSet() throws IOException, URISyntaxException, ClientException {
        PmdGrader pmdGrader = PmdGrader.createFromRuleSetPaths(
                PENALTY_PER_VIOLATION,
                MAX_PENALTY,
                "category/java/documentation.xml");
        List<Result> results = pmdGrader.grade(TestUtilities.getPath("MissingComments.java"));
        assertEquals(1, results.size());
        assertEquals(MAX_PENALTY - 3 * PENALTY_PER_VIOLATION, TestUtilities.getTotalScore(results));
    }

    @Test
    public void testTwoRuleSets() throws IOException, URISyntaxException, ClientException {
        PmdGrader pmdGrader = PmdGrader.createFromRuleSetPaths(
                PENALTY_PER_VIOLATION,
                MAX_PENALTY,
                "category/java/documentation.xml",
                "category/java/codestyle.xml");
        List<Result> results = pmdGrader.grade(TestUtilities.getPath("MissingComments.java"));
        assertEquals(1, results.size());
        assertEquals(MAX_PENALTY - 5 * PENALTY_PER_VIOLATION, TestUtilities.getTotalScore(results));
    }
}
