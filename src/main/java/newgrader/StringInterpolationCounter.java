package newgrader;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class StringInterpolationCounter extends Counter {
    private final VoidVisitorAdapter<MutableInteger> adapter;

    public StringInterpolationCounter(String name, int maxScore, int minCount, int maxCount) {
        super(name, "string interpolations", maxScore, minCount, maxCount);
        adapter = new StringInterpolationAdapter();
    }

    @Override
    public VoidVisitorAdapter<MutableInteger> getAdapter() {
        return adapter;
    }

    private static class StringInterpolationAdapter extends VoidVisitorAdapter<MutableInteger> {
        @Override
        public void visit(MethodCallExpr node, MutableInteger mi) {
            if (node.getScope().isPresent()) {
                String fullMethodName = node.getScope().get().toString() + "." + node.getNameAsString();
                if ((fullMethodName.equals("System.out.printf") || fullMethodName.equals("String.format"))
                        && node.getArguments().size() > 1) {
                    mi.increment();
                }
            }
        }
    }
}
