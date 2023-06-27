package newgrader;

import newgrader.common.*;
import newgrader.syntaxgrader.*;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringInterpolationCounterTest {
    @Test
    public void counterTest() throws URISyntaxException {
        SyntaxCounter counter = new StringInterpolationCounter("String interpolation counter", 1, 2, Integer.MAX_VALUE);
        Target target = TestUtilities.getTargetFromResource("good/Mob.java");
        List<Result> results = counter.grade(target);
        assertEquals(1, results.size());
        assertEquals(1.0, TestUtilities.getTotalScore(results));
    }
}
