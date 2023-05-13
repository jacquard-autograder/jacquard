package newgrader;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.expr.SwitchExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private final JavaParser parser;
    private final List<Checker> checkers = new ArrayList<>();
    private final List<Counter> counters = new ArrayList<>();

    public Main() {
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        parser = new JavaParser(config);

        checkers.add(FieldModifierChecker.makeChecker(
                1.0,
                List.of("type", "behavior", "minDamage", "maxDamage", "maxHearts"),
                List.of(Modifier.finalModifier(), Modifier.privateModifier()),
                List.of()));
        counters.add(new ExpressionCounter("SwitchExpressionCounter", 2, 1, Integer.MAX_VALUE, SwitchExpr.class));
    }

    public void check(InputStream is) {
        ParseResult<CompilationUnit> parseResult = parser.parse(is);
        System.out.println(parseResult);
        if (parseResult.isSuccessful()) {
            CompilationUnit cu = parseResult.getResult().get();
            List<Result> results = new ArrayList<>();
            for (Checker checker : checkers) {
                checker.getAdapter().visit(cu, results);
            }
            for (Counter counter : counters) {
                MutableInteger mi = new MutableInteger();
                VoidVisitorAdapter<MutableInteger> adapter = counter.getAdapter();
                adapter.visit(cu, mi);
                System.out.println(counter.getResult(mi));
            }
            for (Result result : results) {
                System.out.println(result);
            }
        } else {
            System.err.println("Parse failed.");
        }
    }

    public static void main(String[] args) {
        Main checker = new Main();
        checker.check(Main.class.getClassLoader().getResourceAsStream("Mob.java"));
    }
}
