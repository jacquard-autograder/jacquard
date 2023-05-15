package newgrader;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks whether the correct modifiers are used for fields (instance and
 * static variables).
 */
public class FieldModifierChecker extends SyntaxChecker {
    private final List<String> varNames;

    public FieldModifierChecker(String name, double maxScore, List<String> varNames, List<Modifier> requiredModifiers, List<Modifier> optionalModifiers) {
        super(name, maxScore);
        this.varNames = varNames;
        adapter = new Adapter(requiredModifiers, optionalModifiers);
    }

    /**
     * Creates a field modifier checker.
     *
     * @param name the name, which is used in the {@link Result}
     * @param maxScore the maximum score for each variable
     * @param varNames the names of the variables to check
     * @param requiredModifiers modifiers that should be used on each variable
     * @param optionalModifiers modifiers that may be used on variables
     * @return a new instance
     */
    public static FieldModifierChecker makeChecker(
            String name,
            double maxScore,
            List<String> varNames,
            List<Modifier> requiredModifiers,
            List<Modifier> optionalModifiers
    ) {
        return new FieldModifierChecker(
                name,
                maxScore,
                varNames,
                requiredModifiers,
                optionalModifiers
        );
    }

    /**
     * Creates a field modifier checker with a default name.
     *
     * @param maxScore the maximum score for each variable
     * @param varNames the names of the variables to check
     * @param requiredModifiers modifiers that should be used on each variable
     * @param optionalModifiers modifiers that may be used on variables
     * @return a new instance
     */
    public static FieldModifierChecker makeChecker(
            double maxScore,
            List<String> varNames,
            List<Modifier> requiredModifiers,
            List<Modifier> optionalModifiers
    ) {
        return makeChecker(
                null,
                maxScore,
                varNames, requiredModifiers, optionalModifiers
        );
    }

    @Override
    public double getTotalMaxScore() {
        return maxScore * varNames.size();
    }

    private class Adapter extends VoidVisitorAdapter<List<Result>> {
        private final List<Modifier> requiredModifiers;
        private final List<Modifier> optionalModifiers;

        private Adapter(List<Modifier> requiredModifiers, List<Modifier> optionalModifiers) {
            this.requiredModifiers = new ArrayList<>(requiredModifiers);
            this.optionalModifiers = new ArrayList<>(optionalModifiers);
        }

        @Override
        public void visit(VariableDeclarator vd, List<Result> collector) {
            try {
                if (isField(vd) && varNames.contains(vd.getNameAsString())) {
                    FieldDeclaration fd = getFieldDeclaration(vd);

                    // Make a copy of this instance variable's modifiers.
                    List<Modifier> modifiers = new ArrayList<>(fd.getModifiers());

                    // Ensure that all required modifiers are present, removing them.
                    for (Modifier modifier : requiredModifiers) {
                        if (modifiers.contains(modifier)) {
                            modifiers.remove(modifier);
                        } else {
                            collector.add(
                                    makeFailingResult(
                                            String.format(
                                                    "%s is missing required modifier '%s'.",
                                                    getDeclarationDescription(fd, vd),
                                                    modifier.toString().trim())));
                            return;
                        }
                    }

                    // Ensure that any remaining modifiers are permitted.
                    for (Modifier modifier : modifiers) {
                        if (!optionalModifiers.contains(modifier)) {
                            collector.add(
                                    makeFailingResult(
                                            String.format(
                                                    "%s has forbidden modifier '%s'.",
                                                    getDeclarationDescription(fd, vd),
                                                    modifier.toString().trim())));
                            return;
                        }
                    }
                    collector.add(makeSuccessResult(
                            String.format(
                                    "%s is correct.",
                                    getDeclarationDescription(fd, vd),
                                    getEnclosingClassName(fd))));
                }
            } finally {
                super.visit(vd, collector);
            }
        }
    }
}
