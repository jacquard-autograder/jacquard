package com.spertus.jacquard.syntaxgrader;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.spertus.jacquard.exceptions.ClientException;

import java.util.function.Predicate;

/**
 * A grader that counts whether the number of parse nodes satisfying a given
 * predicate is in the specified range.
 *
 * @see com.github.javaparser.ast.Node
 */
public abstract class SyntaxConditionCountGrader extends SyntaxCountGrader {
    /**
     * Creates a new counter to test whether the number of parse nodes
     * satisfying the predicate is within the specified range.
     *
     * @param name        the name of this grader
     * @param countedName the name of the element(s) being counted
     * @param maxScore    the score if the condition holds
     * @param minCount    the minimum number of occurrences, which must be non-negative
     * @param maxCount    the maximum number of occurrences, or {@link Integer#MAX_VALUE}
     *                    if there is no limit
     * @param predicate   the condition
     * @throws ClientException if minCount &lt; 0, maxCount &lt; minCount,
     *                         or minCount is 0 when maxCount is {@link Integer#MAX_VALUE}
     */
    public SyntaxConditionCountGrader(
            final String name,
            final String countedName,
            final double maxScore,
            final int minCount,
            final int maxCount,
            final Predicate<Node> predicate)
            throws ClientException {
        super(name, countedName, maxScore, minCount, maxCount, new Adapter(predicate));
    }

    private static class Adapter extends VoidVisitorAdapter<MutableInteger> { // NOPMD
        private final Predicate<Node> predicate;

        private Adapter(final Predicate<Node> predicate) {
            super();
            this.predicate = predicate;
        }

        private void count(final Node node, final MutableInteger mi) {
            if (predicate.test(node)) {
                mi.increment();
            }
        }

        @Override
        public void visit(final AnnotationDeclaration n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final AnnotationMemberDeclaration n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ArrayAccessExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ArrayCreationExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ArrayInitializerExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final AssertStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final AssignExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final BinaryExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final BlockComment n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final BlockStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final BooleanLiteralExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final BreakStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final CastExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final CatchClause n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final CharLiteralExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ClassExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ClassOrInterfaceDeclaration n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ClassOrInterfaceType n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final CompilationUnit n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ConditionalExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ConstructorDeclaration n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ContinueStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final DoStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final DoubleLiteralExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final EmptyStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final EnclosedExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final EnumConstantDeclaration n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final EnumDeclaration n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ExplicitConstructorInvocationStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ExpressionStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final FieldAccessExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final FieldDeclaration n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ForEachStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ForStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final IfStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final InitializerDeclaration n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final InstanceOfExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final IntegerLiteralExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final JavadocComment n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final LabeledStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final LineComment n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final LongLiteralExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final MarkerAnnotationExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final MemberValuePair n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final MethodCallExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final MethodDeclaration n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final NameExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final NormalAnnotationExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final NullLiteralExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ObjectCreationExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final PackageDeclaration n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final Parameter n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final PrimitiveType n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final Name n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final SimpleName n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ArrayType n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ArrayCreationLevel n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final IntersectionType n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final UnionType n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ReturnStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final SingleMemberAnnotationExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final StringLiteralExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final SuperExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final SwitchEntry n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final SwitchStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final SynchronizedStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ThisExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ThrowStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final TryStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final LocalClassDeclarationStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final LocalRecordDeclarationStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final TypeParameter n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final UnaryExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final UnknownType n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final VariableDeclarationExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final VariableDeclarator n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final VoidType n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final WhileStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final WildcardType n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final LambdaExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final MethodReferenceExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final TypeExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ImportDeclaration n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ModuleDeclaration n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ModuleRequiresDirective n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ModuleExportsDirective n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ModuleProvidesDirective n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ModuleUsesDirective n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ModuleOpensDirective n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final UnparsableStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final ReceiverParameter n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final VarType n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final Modifier n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final SwitchExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final TextBlockLiteralExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final YieldStmt n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final PatternExpr n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final RecordDeclaration n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }

        @Override
        public void visit(final CompactConstructorDeclaration n, final MutableInteger mi) {
            count(n, mi);
            super.visit(n, mi);
        }
    }
}
