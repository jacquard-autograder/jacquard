package newgrader;

import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Optional;

public class ExpressionCounter extends Counter {
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
