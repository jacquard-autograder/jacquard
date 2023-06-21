package newgrader.syntaxgrader;

import com.github.javaparser.ast.stmt.*;
import newgrader.common.Result;
import newgrader.exceptions.ClientException;

import java.util.List;

/**
 * A counter to test whether the number of occurrences of a given statement
 * type is within the specified range.
 */
public class StatementCounter extends ExpressionStatementCounter {

    /**
     * Creates a new counter to test whether the number of occurrences of the
     * statement type is within the specified range.
     *
     * @param name     the name of this processor (for the {@link Result})
     * @param maxScore the score if the condition holds
     * @param minCount the minimum number of occurrences, which must be non-negative
     * @param maxCount the maximum number of occurrences, or {@link Integer#MAX_VALUE}
     *                 if there is no limit
     * @param clazz    the statement class
     * @throws ClientException if minCount &lt; 0, maxCount &lt; minCount,
     *                         , or minCount is 0 when maxCount is {@link Integer#MAX_VALUE}
     */
    public StatementCounter(
            String name,
            double maxScore,
            int minCount,
            int maxCount,
            Class<? extends Statement> clazz
    ) throws ClientException {
        super(name, clazz.getSimpleName(), maxScore, minCount, maxCount, List.of(), List.of(clazz));
    }
}
