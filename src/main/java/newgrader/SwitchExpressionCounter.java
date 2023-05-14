package newgrader;

import com.github.javaparser.ast.expr.SwitchExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class SwitchExpressionCounter extends Counter {
    VoidVisitorAdapter<MutableInteger> adapter;

    public SwitchExpressionCounter(double maxScore, int minCount, int maxCount) {
        super("switch expression counter", "switch expression", maxScore, minCount, maxCount);
        adapter = new VoidVisitorAdapter<>() {
            @Override
            public void visit(SwitchExpr node, MutableInteger mi) {
                mi.increment();
            }
        };
    }

    @Override
    protected VoidVisitorAdapter<MutableInteger> getAdapter() {
        return adapter;
    }
}
