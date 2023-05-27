package newgrader;

import newgrader.crossgrader.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrossGraderTest {
    private static final String CSV_FILE = """
            , newgrader.crossgrader.CorrectAdder, newgrader.crossgrader.BuggyAdder
            addZero, 2, -1
            """;
    private CrossGrader grader;

    @BeforeEach
    public void setup() {
        grader = new CrossGrader(GeneralizedAdderTest.class,
                new ByteArrayInputStream(CSV_FILE.getBytes()));
    }

    @Test
    public void testAll() {
        List<Result> results = grader.gradeAll();
        assertEquals(2, results.size());
        assertEquals(3, TestUtilities.getTotalScore(results));
    }

}
