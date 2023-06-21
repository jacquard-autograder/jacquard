package newgrader.syntaxgrader;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import newgrader.exceptions.ClientException;

import java.util.List;

/**
 * A counter to test whether the number of occurrences of the given
 * expression and statement types are in the specified range.
 */
public class ExpressionStatementCounter extends SyntaxConditionCounter {
    public ExpressionStatementCounter(
            String name,
            String countedName,
            double maxScore,
            int minCount,
            int maxCount,
            List<Class<? extends Expression>> expressionClasses,
            List<Class<? extends Statement>> statementClasses)
            throws ClientException {
        super(name, countedName, maxScore, minCount, maxCount,
                node -> {
                    if (node instanceof Expression) {
                        for (Class<?> clazz : expressionClasses) {
                            if (clazz.isInstance(node)) {
                                return true;
                            }
                        }
                    } else if (node instanceof Statement) {
                        for (Class<?> clazz : statementClasses) {
                            if (clazz.isInstance(node)) {
                                return true;
                            }
                        }
                    }
                    return false;
                });
    }
}
