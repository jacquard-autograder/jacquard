package newgrader.syntaxgrader;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import newgrader.Result;

/**
 * A counter to test whether the number of occurrences of a given expression
 * type is within the specified range.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class ExpressionCounter extends Counter {

    /**
     * Creates a new counter to test whether the number of occurrences of an
     * expression type is within the specified range.
     *
     * @param name     the name of this processor (for the {@link Result})
     * @param maxScore the score if the condition holds
     * @param minCount the minimum number of occurrences
     * @param maxCount the maximum number of occurrences, or {@link Integer#MAX_VALUE}
     *                 if there is no limit
     * @param clazz    the expression class
     * @throws IllegalArgumentException if minCount &lt; 0, maxCount &lt; minCount,
     *                                  or minCount is 0 when maxCount is {@link Integer#MAX_VALUE}
     */
    public ExpressionCounter(String name, double maxScore, int minCount, int maxCount, Class<? extends Expression> clazz) {
        super(name, clazz.getSimpleName(), maxScore, minCount, maxCount, new ExpressionAdapter(clazz));
    }

    private static class ExpressionAdapter extends VoidVisitorAdapter<MutableInteger> {
        private final Class<? extends Expression> clazz;

        private ExpressionAdapter(Class<? extends Expression> clazz) {
            super();
            this.clazz = clazz;
        }

        private void check(Expression expression, MutableInteger mi) {
            if (clazz.isInstance(expression)) {
                mi.increment();
            }
        }

        // Subclasses of AnnotationExpr
        @Override
        public void visit(MarkerAnnotationExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(NormalAnnotationExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(SingleMemberAnnotationExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ArrayAccessExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ArrayCreationExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ArrayInitializerExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(AssignExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(BinaryExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(CastExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ClassExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ConditionalExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(EnclosedExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(FieldAccessExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(InstanceOfExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(LambdaExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        // Subclasses of LiteralExpr
        @Override
        public void visit(BooleanLiteralExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(CharLiteralExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(DoubleLiteralExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(IntegerLiteralExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(LongLiteralExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(StringLiteralExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(TextBlockLiteralExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(NullLiteralExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(MethodCallExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(MethodReferenceExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(NameExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ObjectCreationExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(PatternExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(SuperExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(SwitchExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ThisExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(TypeExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(UnaryExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(VariableDeclarationExpr node, MutableInteger mi) {
            check(node, mi);
            super.visit(node, mi);
        }
    }
}
