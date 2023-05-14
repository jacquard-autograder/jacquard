package newgrader;

import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Optional;

/**
 * A counter to test whether the number of occurrences of a given expression
 * type is within the specified range.
 */
public class ExpressionCounter extends Counter {
    /**
     * Creates a new counter to test whether the number of occurrences of an
     * expression type is within the specified range.
     *
     * @param name     the name of this processor (for the {@link Result})
     * @param maxScore the score if the condition holds
     * @param minCount the minimum number of occurrences
     * @param maxCount the maximum number of occurrences, or {@link Integer#MAX_VALUE}
     *                 if there is no limit.
     * @param clazz    the expression class
     * @throws IllegalArgumentException if minCount < 0 or maxCount < minCount,
     *                                  or if minCount is 0 when maxCount is {@link Integer#MAX_VALUE}
     */
    public ExpressionCounter(String name, int maxScore, int minCount, int maxCount, Class<? extends Expression> clazz) {
        super(name, clazz.getSimpleName(), maxScore, minCount, maxCount);
        adapter = new ExpressionAdapter(clazz);
    }

    private static class ExpressionAdapter extends VoidVisitorAdapter<MutableInteger> {
        private final Class<? extends Expression> clazz;

        private ExpressionAdapter(Class<? extends Expression> clazz) {
            this.clazz = clazz;
        }

        private void check(Expression expression, MutableInteger mi) {
            if (clazz.isInstance(expression)) {
                mi.increment();
            }
        }

        private void check(Optional<Expression> expression, MutableInteger mi) {
            if (expression.isPresent() && clazz.isInstance(expression.get())) {
                mi.increment();
            }
        }

        // To decide what visitor overloads to create, I searched
        // the javadoc for getExpression() and classes that implement
        // NodeWithExpression<?>.
        @Override
        public void visit(CastExpr node, MutableInteger mi) {
            check(node.getExpression(), mi);
        }

        @Override
        public void visit(ExplicitConstructorInvocationStmt node, MutableInteger mi) {
            check(node.getExpression(), mi);
        }

        @Override
        public void visit(ExpressionStmt node, MutableInteger mi) {
            check(node.getExpression(), mi);
        }

        @Override
        public void visit(InstanceOfExpr node, MutableInteger mi) {
            check(node.getExpression(), mi);
        }

        @Override
        public void visit(ReturnStmt node, MutableInteger mi) {
            check(node.getExpression(), mi);
        }

        @Override
        public void visit(SynchronizedStmt node, MutableInteger mi) {
            check(node.getExpression(), mi);
        }

        @Override
        public void visit(ThrowStmt node, MutableInteger mi) {
            check(node.getExpression(), mi);
        }

        @Override
        public void visit(UnaryExpr node, MutableInteger mi) {
            check(node.getExpression(), mi);
        }

        @Override
        public void visit(YieldStmt node, MutableInteger mi) {
            check(node.getExpression(), mi);
        }
    }
}
