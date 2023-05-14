package newgrader;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Optional;

/**
 * A counter to test whether the number of occurrences of a given expression
 * type is within the specified range.
 */
public class ExpressionCounter extends Counter {
    private final VoidVisitorAdapter<MutableInteger> adapter;

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
    public ExpressionCounter(String name, double maxScore, int minCount, int maxCount, Class<? extends Expression> clazz) {
        super(name, clazz.getSimpleName(), maxScore, minCount, maxCount);
        adapter = new ExpressionAdapter(clazz);
    }

    @Override
    protected VoidVisitorAdapter<MutableInteger> getAdapter() {
        return adapter;
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

        private void check(NodeList<? extends Expression> expressions, MutableInteger mi) {
            for (Expression expression : expressions) {
                if (clazz.isInstance(expression)) {
                    mi.increment();
                }
            }
        }

        private void check(Optional<? extends Expression> expression, MutableInteger mi) {
            if (expression.isPresent() && clazz.isInstance(expression.get())) {
                mi.increment();
            }
        }

        // expressions
        @Override
        public void visit(ArrayAccessExpr node, MutableInteger mi) {
            check(node.getName(), mi);
            check(node.getIndex(), mi);
        }

        @Override
        public void visit(ArrayCreationExpr node, MutableInteger mi) {
            check(node.getInitializer(), mi);
        }

        @Override
        public void visit(AssignExpr node, MutableInteger mi) {
            check(node.getTarget(), mi);
            check(node.getValue(), mi);
        }

        @Override
        public void visit(BinaryExpr node, MutableInteger mi) {
            check(node.getLeft(), mi);
            check(node.getRight(), mi);
        }

        @Override
        public void visit(CastExpr node, MutableInteger mi) {
            check(node.getExpression(), mi);
        }

        @Override
        public void visit(ConditionalExpr node, MutableInteger mi) {
            check(node.getCondition(), mi);
            check(node.getElseExpr(), mi);
            check(node.getThenExpr(), mi);
        }

        @Override
        public void visit(EnclosedExpr node, MutableInteger mi) {
            check(node.getInner(), mi);
        }

        @Override
        public void visit(FieldAccessExpr node, MutableInteger mi) {
            check(node.getScope(), mi);
        }

        @Override
        public void visit(InstanceOfExpr node, MutableInteger mi) {
            check(node.getExpression(), mi);
        }

        @Override
        public void visit(LambdaExpr node, MutableInteger mi) {
            check(node.getExpressionBody(), mi);
        }

        @Override
        public void visit(MethodCallExpr node, MutableInteger mi) {
            check(node.getScope(), mi);
            check(node.getArguments(), mi);
        }

        @Override
        public void visit(MethodReferenceExpr node, MutableInteger mi) {
            check(node.getScope(), mi);
        }

        @Override
        public void visit(ObjectCreationExpr node, MutableInteger mi) {
            check(node.getScope(), mi);
            check(node.getArguments(), mi);
        }

        @Override
        public void visit(SwitchExpr node, MutableInteger mi) {
            check(node.getSelector(), mi);
        }

        @Override
        public void visit(UnaryExpr node, MutableInteger mi) {
            check(node.getExpression(), mi);
        }

        @Override
        public void visit(VariableDeclarationExpr node, MutableInteger mi) {
            check(node.getAnnotations(), mi);
        }

        // statements

        @Override
        public void visit(AssertStmt node, MutableInteger mi) {
            check(node.getCheck(), mi);
        }

        @Override
        public void visit(DoStmt node, MutableInteger mi) {
            check(node.getCondition(), mi);
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
        public void visit(ForEachStmt node, MutableInteger mi) {
            check(node.getIterable(), mi);
        }

        @Override
        public void visit(IfStmt node, MutableInteger mi) {
            check(node.getCondition(), mi);
        }

        @Override
        public void visit(ReturnStmt node, MutableInteger mi) {
            check(node.getExpression(), mi);
        }

        @Override
        public void visit(SwitchStmt node, MutableInteger mi) {
            check(node.getSelector(), mi);
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
        public void visit(WhileStmt node, MutableInteger mi) {
            check(node.getCondition(), mi);
        }

        @Override
        public void visit(YieldStmt node, MutableInteger mi) {
            check(node.getExpression(), mi);
        }
    }
}
