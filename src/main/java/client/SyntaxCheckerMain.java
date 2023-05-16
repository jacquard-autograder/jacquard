package client;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.expr.SwitchExpr;
import com.github.javaparser.ast.stmt.SwitchStmt;
import newgrader.*;

import java.util.List;

public class SyntaxCheckerMain {
    public static void main(String[] args) {
        Autograder autograder = new Autograder(ParserConfiguration.LanguageLevel.JAVA_17);
        autograder.addProcessor(
                new FieldModifierChecker("Private/final check", 1.0,
                        List.of("behavior", "maxHearts", "maxDamage", "minDamage", "type"),
                        List.of(Modifier.finalModifier(), Modifier.privateModifier()),
                        List.of()));
        autograder.addProcessor(
                new MaxSyntaxGrader(List.of(
                        new ExpressionCounter(
                                "Switch expression check", 1, 1, Integer.MAX_VALUE, SwitchExpr.class),
                        new StatementCounter(
                                "Switch statement check", 1, 1, Integer.MAX_VALUE, SwitchStmt.class))));
        autograder.addProcessor(
                new StringInterpolationCounter("String interpolation counter", 1, 2, Integer.MAX_VALUE));
        List<Result> results = autograder.grade(SyntaxCheckerMain.class.getClassLoader().getResourceAsStream("Mob.java"));
        for (Result result : results) {
            System.out.println(result);
        }
    }
}
