package newgrader;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.SwitchExpr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void scoreIsMaxIfRightNumber() {
        CompilationUnit cu = TestUtilities.parse("""
                if (x instanceof String) {
                    System.out.println(x);
                }
                """);
        List<Result> results = counter.process(cu);
        assertEquals(1, results.size());
        assertEquals(MAX_SCORE, results.get(0).score());
    }
}
