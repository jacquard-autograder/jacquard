package newgrader.syntaxgrader;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import newgrader.exceptions.ClientException;

import java.util.List;

public class ExpressionStatementCounter extends SyntaxCounter {
    public ExpressionStatementCounter(
            String name,
            String countedName,
            double maxScore,
            int minCount,
            int maxCount,
            List<Class<? extends Expression>> expressionClasses,
            List<Class<? extends Statement>> statementClasses)
            throws ClientException {
        super(name, countedName, maxScore, minCount, maxCount, new ExpressionStatementAdapter(expressionClasses, statementClasses));
    }

    private static class ExpressionStatementAdapter extends VoidVisitorAdapter<MutableInteger> {
        private final List<Class<? extends Expression>> expressionClasses;
        private final List<Class<? extends Statement>> statementClasses;

        private ExpressionStatementAdapter(List<Class<? extends Expression>> expressionClasses, List<Class<? extends Statement>> statementClasses) {
            super();
            this.expressionClasses = expressionClasses;
            this.statementClasses = statementClasses;
        }

        private void count(Node node, MutableInteger mi) {
            if (node instanceof Expression) {
                for (Class<?> clazz : expressionClasses) {
                    if (clazz.isInstance(node)) {
                        mi.increment();
                        return;
                    }
                }
            }
            else if (node instanceof Statement) {
                for (Class<?> clazz : statementClasses) {
                    if (clazz.isInstance(node)) {
                        mi.increment();
                        return;
                    }
                }
            }
        }

        // Expressions
        @Override
        public void visit(MarkerAnnotationExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(NormalAnnotationExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(SingleMemberAnnotationExpr node, MutableInteger
                mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ArrayAccessExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ArrayCreationExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ArrayInitializerExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(AssignExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(BinaryExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(CastExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ClassExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ConditionalExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(EnclosedExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(FieldAccessExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(InstanceOfExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(LambdaExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(BooleanLiteralExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(CharLiteralExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(DoubleLiteralExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(IntegerLiteralExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(LongLiteralExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(StringLiteralExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(TextBlockLiteralExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(NullLiteralExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(MethodCallExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(MethodReferenceExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(NameExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ObjectCreationExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(PatternExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(SuperExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(SwitchExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(ThisExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(TypeExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(UnaryExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        @Override
        public void visit(VariableDeclarationExpr node, MutableInteger mi) {
            count(node, mi);
            super.visit(node, mi);
        }

        // Statements

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
