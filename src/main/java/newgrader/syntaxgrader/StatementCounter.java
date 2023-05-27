package newgrader.syntaxgrader;

import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import newgrader.Result;
import newgrader.exceptions.ClientException;

/**
 * A counter to test whether the number of occurrences of a given statement
 * type is within the specified range.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class StatementCounter extends Counter {

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
        super(name, clazz.getSimpleName(), maxScore, minCount, maxCount, new StatementAdapter(clazz));
    }

    private static class StatementAdapter extends VoidVisitorAdapter<MutableInteger> {
        private final Class<? extends Statement> clazz;

        private StatementAdapter(Class<? extends Statement> clazz) {
            super();
            this.clazz = clazz;
        }

        private void count(Statement statement, MutableInteger mi) {
            if (clazz.isInstance(statement)) {
                mi.increment();
            }
        }

        @Override
        public void visit(AssertStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(BlockStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(BreakStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ContinueStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(DoStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(EmptyStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ExplicitConstructorInvocationStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ExpressionStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ForEachStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ForStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(IfStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(LabeledStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(LocalClassDeclarationStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(LocalRecordDeclarationStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ReturnStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(SwitchStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(SynchronizedStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ThrowStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(TryStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(UnparsableStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(WhileStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(YieldStmt node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }
    }
}
