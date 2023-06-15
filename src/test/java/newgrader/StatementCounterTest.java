package newgrader;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.*;
import newgrader.common.Result;
import newgrader.exceptions.ClientException;
import newgrader.syntaxgrader.StatementCounter;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatementCounterTest {
    private static final double MAX_SCORE = 2.5;
    private static final String SAMPLE_LOOP_CODE = """
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
            }
            for (String s : myStrings) {}
            for (int i : myInts) {
                System.out.println(i);
            }
            """;
    private static CompilationUnit sampleLoopParsed;

    @BeforeAll
    public static void setup() {
        sampleLoopParsed = TestUtilities.parseProgramFromStatements(SAMPLE_LOOP_CODE);
    }

    private void testHelper(CompilationUnit cu, int actualCount, int minCount, int maxCount, Class<? extends Statement> statementType)
            throws ClientException {
        StatementCounter counter = new StatementCounter(
                statementType.getSimpleName() + " counter",
                MAX_SCORE, minCount, maxCount, statementType);
        List<Result> results = counter.grade(cu);
        assertEquals(1, results.size());
        assertEquals(actualCount >= minCount && actualCount <= maxCount ? MAX_SCORE : 0, results.get(0).score());
    }

    private void testTooFew(CompilationUnit cu, int actualCount, Class<? extends Statement> statementType) throws ClientException {
        testHelper(cu, actualCount, actualCount + 1, actualCount + 10, statementType);
        testHelper(cu, actualCount, actualCount + 1, Integer.MAX_VALUE, statementType);
    }

    // actualCount must be > 0
    private void testTooMany(CompilationUnit cu, int actualCount, Class<? extends Statement> statementType) throws ClientException {
        testHelper(cu, actualCount, 0, actualCount - 1, statementType);
    }

    private void testRightNumber(CompilationUnit cu, int actualCount, Class<? extends Statement> statementType) throws ClientException {
        testHelper(cu, actualCount, 0, actualCount, statementType);
        testHelper(cu, actualCount, actualCount, Integer.MAX_VALUE, statementType);
    }

    private void testAllPossibilities(CompilationUnit cu, int actualCount, Class<? extends Statement> statementType) throws ClientException {
        testTooFew(cu, actualCount, statementType);
        testTooMany(cu, actualCount, statementType);
        testRightNumber(cu, actualCount, statementType);
    }

    @Test
    public void testForStatementCounter() throws ClientException {
        testAllPossibilities(sampleLoopParsed, 1, ForStmt.class);
    }

    @Test
    public void testAssignmentStatementCounter() throws ClientException {
        testAllPossibilities(sampleLoopParsed, 2, ForEachStmt.class);
    }
}
