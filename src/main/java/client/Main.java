package client;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.expr.SwitchExpr;
import newgrader.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Autograder autograder = new Autograder(ParserConfiguration.LanguageLevel.JAVA_17);
        autograder.addProcessor(
                new FieldModifierChecker("Private/final check", 1.0,
                        List.of("behavior", "maxHearts", "maxDamage", "minDamage", "type"),
                        List.of(Modifier.finalModifier(), Modifier.privateModifier()),
                        List.of()));
        autograder.addProcessor(
                new ExpressionCounter(
                        "Switch expression check", 1, 1, Integer.MAX_VALUE, SwitchExpr.class));
        autograder.addProcessor(
                new StringInterpolationCounter("String interpolation counter", 1, 3, Integer.MAX_VALUE));
        List<Result> results = autograder.grade(Main.class.getClassLoader().getResourceAsStream("Mob.java"));
        for (Result result : results) {
            System.out.println(result);
        }
    }
}
