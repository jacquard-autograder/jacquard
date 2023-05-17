package newgrader;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.*;
import newgrader.syntaxgrader.ExpressionCounter;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpressionCounterTest {
    private static final String NAME = "name";
    private static final double MAX_SCORE = 10.0;

    private ExpressionCounter counter;

    @BeforeEach
    public void setup() {
        counter = new ExpressionCounter(NAME, MAX_SCORE, 1, 2, InstanceOfExpr.class);
    }

    @Test
    public void constructorThrowsExceptions() {
        // minimum is too low
        assertThrows(IllegalArgumentException.class,
                () -> new ExpressionCounter(NAME, MAX_SCORE, -1, 2, SwitchExpr.class));

        // maximum is lower than minimum
        assertThrows(IllegalArgumentException.class,
                () -> new ExpressionCounter(NAME, MAX_SCORE, 3, 2, SwitchExpr.class));

        // any count is allowed
        assertThrows(IllegalArgumentException.class,
                () -> new ExpressionCounter(NAME, MAX_SCORE, 0, Integer.MAX_VALUE, SwitchExpr.class));
    }

    private void testHelper(CompilationUnit cu, int actualCount, int minCount, int maxCount, Class<? extends Expression> expressionType) {
        ExpressionCounter counter = new ExpressionCounter(
                expressionType.getSimpleName() + " counter",
                MAX_SCORE, minCount, maxCount, expressionType);
        List<Result> results = counter.grade(cu);
        assertEquals(1, results.size());
        assertEquals(actualCount >= minCount && actualCount <= maxCount ? MAX_SCORE : 0, results.get(0).score());
    }

    private void testTooFew(CompilationUnit cu, int actualCount, Class<? extends Expression> expressionType) {
        testHelper(cu, actualCount, actualCount + 1, actualCount + 10, expressionType);
        testHelper(cu, actualCount, actualCount + 1, Integer.MAX_VALUE, expressionType);
    }

    // actualCount must be > 0
    private void testTooMany(CompilationUnit cu, int actualCount, Class<? extends Expression> expressionType) {
        testHelper(cu, actualCount, 0, actualCount - 1, expressionType);
    }

    private void testRightNumber(CompilationUnit cu, int actualCount, Class<? extends Expression> expressionType) {
        testHelper(cu, actualCount, 0, actualCount, expressionType);
        testHelper(cu, actualCount, actualCount, Integer.MAX_VALUE, expressionType);
    }

    private void testAllPossibilities(CompilationUnit cu, int actualCount, Class<? extends Expression> expressionType) {
        testTooFew(cu, actualCount, expressionType);
        testTooMany(cu, actualCount, expressionType);
        testRightNumber(cu, actualCount, expressionType);
    }

    @Test
    public void testInstanceOfExprCounter() {
        CompilationUnit cu = TestUtilities.parseProgramFromStatements("""
                if (x instanceof String) {
                    System.out.println(x);
                }
                """);
        testAllPossibilities(cu, 1, InstanceOfExpr.class);
    }

    @Test
    public void testBinaryExprCounter() {
        CompilationUnit cu = TestUtilities.parseProgramFromStatements("""
                System.out.println(x + y + 1 + 2);
                """);
        testAllPossibilities(cu, 3, BinaryExpr.class);
    }

    @Test
    public void testUnaryExprCounter() {
        CompilationUnit cu = TestUtilities.parseProgramFromStatements("""
                System.out.println(-(-x));
                """);
        testAllPossibilities(cu, 2, UnaryExpr.class);
    }
}
