package newgrader.syntaxgrader;

import com.github.javaparser.ast.expr.SwitchExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class SwitchExpressionCounter extends Counter {
    VoidVisitorAdapter<MutableInteger> adapter;

    public SwitchExpressionCounter(double maxScore, int minCount, int maxCount) {
        super("switch expression counter", "switch expression", maxScore, minCount, maxCount,
                new VoidVisitorAdapter<>() {
                    @Override
                    public void visit(SwitchExpr node, MutableInteger mi) {
                        mi.increment();
                    }
                });
    }
}
