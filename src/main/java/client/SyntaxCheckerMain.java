package client;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.expr.SwitchExpr;
import com.github.javaparser.ast.stmt.SwitchStmt;
import newgrader.common.Result;
import newgrader.exceptions.ClientException;
import newgrader.syntaxgrader.*;

import java.util.List;

public class SyntaxCheckerMain {
    public static void main(String[] args) throws ClientException {
        Parser parser = new Parser(ParserConfiguration.LanguageLevel.JAVA_17);
        parser.addProcessor(
                new FieldModifierChecker("Private/final check", 1.0,
                        List.of("behavior", "maxHearts", "maxDamage", "minDamage", "type"),
                        List.of(Modifier.finalModifier(), Modifier.privateModifier()),
                        List.of()));
        parser.addProcessor(
                new StringInterpolationCounter("String interpolation counter", 1, 2, Integer.MAX_VALUE));
        List<Result> results = parser.grade(SyntaxCheckerMain.class.getClassLoader().getResourceAsStream("Mob.java"));
        for (Result result : results) {
            System.out.println(result);
        }
    }
}
