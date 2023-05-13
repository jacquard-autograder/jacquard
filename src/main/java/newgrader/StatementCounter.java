package newgrader;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithStatements;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class StatementCounter extends Counter {
    public StatementCounter(String name, int maxScore, int minCount, int maxCount, Class<? extends Statement> clazz) {
        super(name, clazz.getSimpleName(), maxScore, minCount, maxCount);
        adapter = new StatementAdapter(clazz);
    }

    private static class StatementAdapter extends VoidVisitorAdapter<MutableInteger> {
        private final Class<? extends Statement> clazz;

        private StatementAdapter(Class<? extends Statement> clazz) {
            this.clazz = clazz;
        }

        private void count(NodeWithStatements<? extends Node> node, MutableInteger mi) {
            for (Statement statement : node.getStatements()) {
                if (clazz.isInstance(statement)) {
                    mi.increment();
                }
            }
        }

        @Override
        public void visit(BlockStmt node, MutableInteger mi) {
            count(node, mi);
        }

        @Override
        public void visit(SwitchEntry node, MutableInteger mi) {
            count(node, mi);
        }

        @Override
        public void visit(LabeledStmt ls, MutableInteger mi) {
            if (clazz.isInstance(ls.getStatement())) {
                mi.increment();
            }
        }
    }
}
