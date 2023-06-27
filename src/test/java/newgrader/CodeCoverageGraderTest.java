package newgrader;

import newgrader.common.*;
import newgrader.coverage.*;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeCoverageGraderTest {
    @Test
    public void testLinearScorer() throws URISyntaxException {
        Scorer scorer = new LinearScorer(.5, 10);
        CodeCoverageGrader grader = new CodeCoverageGrader(scorer);
        List<Result> results = grader.grade(TestUtilities.getTargetFromResource("good/PrimeChecker.java"));
        assertEquals(1, results.size());
        assertEquals(7.75, results.get(0).score());
        assertEquals(10.0, results.get(0).maxScore());
    }
}
