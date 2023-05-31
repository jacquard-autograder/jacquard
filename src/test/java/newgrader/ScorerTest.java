package newgrader;

import newgrader.coverage.*;
import newgrader.exceptions.ClientException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ScorerTest {

    @Test
    public void testLinearBranchScorer() {
        Scorer scorer = new LinearBranchScorer();
        assertEquals(95.0, scorer.score(.95, .80, 100));
        assertEquals(15.0, scorer.score(.15, 1.0, 100));
    }

    @Test
    public void testLinearLineScorer() {
        Scorer scorer = new LinearLineScorer();
        assertEquals(80.0, scorer.score(.95, .80, 100));
        assertEquals(100.0, scorer.score(.15, 1.0, 100));
    }

    @ParameterizedTest
    @CsvSource({"2,.5", "1.0,1.0", ".2,.9", "-.5,.5",".2,1.2"})
    public void testLinearScorerThrowsExceptions(double branchWeight, double lineWeight) {
        assertThrows(ClientException.class,
                () -> new LinearScorer(branchWeight, lineWeight));
    }
}
