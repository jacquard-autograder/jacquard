package newgrader;

import com.github.javaparser.ast.CompilationUnit;
import newgrader.common.Result;
import newgrader.exceptions.ClientException;
import newgrader.syntaxgrader.SwitchExpressionCounter;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SwitchExpressionCounterTest {
    private static final double MAX_SCORE = 10.0;

    private SwitchExpressionCounter counter;

    @BeforeEach
    public void setup() throws ClientException {
        counter = new SwitchExpressionCounter(MAX_SCORE, 1, 2);
    }

    @Test
    public void scoreIsMaxIfRightNumber() {
        CompilationUnit cu = TestUtilities.parseProgramFromStatements("""
                return switch (behavior) {
                    case Passive -> false;
                    case Boss, Hostile -> true;
                    case Neutral -> getStatus() == Status.Injured;
                };
                        """);
        List<Result> results = counter.grade(cu);
        assertEquals(1, results.size());
        assertEquals(MAX_SCORE, results.get(0).score());
    }
}
