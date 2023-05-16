package newgrader.syntaxgrader;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import newgrader.Result;

import java.lang.reflect.*;
import java.util.*;

public class OverrideChecker extends SyntaxChecker {
    private final List<Method> methodsToOverride;

    private OverrideChecker(String name, double maxScorePerMethod, List<Method> methodsToOverride) {
        // We can't create the adapter until the superclass constructor has completed.
        super(name, maxScorePerMethod, null);

        // Check that no methods are static or final.
        for (Method method : methodsToOverride) {
            if ((method.getModifiers() & Modifier.FINAL) != 0) {
                throw new IllegalArgumentException(
                        String.format(
                                "Method %s is final and cannot be overridden.", method.getName()));
            }
            if ((method.getModifiers() & Modifier.STATIC) != 0) {
                throw new IllegalArgumentException(
                        String.format(
                                "Method %s is static and cannot be overridden.", method.getName()));
            }
        }
        this.methodsToOverride = methodsToOverride;

        // Now we can create and initialize the adapter.
        adapter = new OverrideCheckerAdapter();
    }

    /**
     * Creates an override checker that ensures that, if methods with the same
     * name as methodsToOverride are found, they have the {@link Override}
     * annotation. This currently checks only method names.
     *
     * @param name              the name of the checker
     * @param maxScorePerMethod the per-method score
     * @param methodsToOverride methods to override
     * @throws IllegalArgumentException if any of the methods are final
     */
    public static OverrideChecker makeOverrideCheckerFromMethodList(
            String name,
            double maxScorePerMethod,
            List<Method> methodsToOverride
    ) {
        return new OverrideChecker(
                name,
                maxScorePerMethod,
                methodsToOverride
        );
    }

    // For now, don't include methods from the supertype's supertype.
    private static OverrideChecker makeOverrideChecker(
            String name,
            double maxScorePerMethod,
            Class<?> supertype,
            int requiredModifiers,  // use &
            int forbiddenModifiers  // use |
    ) {
        Method[] allMethods = supertype.isInterface() ? supertype.getMethods() : supertype.getDeclaredMethods();
        List<Method> methods = Arrays.stream(allMethods)
                .filter(method -> (method.getModifiers() & requiredModifiers) == requiredModifiers
                        && (method.getModifiers() & forbiddenModifiers) == 0)
                .toList();
        return new OverrideChecker(name, maxScorePerMethod, methods);
    }

    /**
     * Creates an override checker that ensures that, if methods with the
     * same names as those in the supertype are found, they have the {@link Override}
     * annotation. Final and static methods are skipped. This currently checks
     * only method names, not argument lists, and does not consider methods
     * declared in the supertype's supertypes.
     *
     * @param name              the name of the checker
     * @param maxScorePerMethod the per-method score
     * @param supertype         the class or interface containing the method declarations
     */
    public static OverrideChecker makeAllAllowableMethodChecker(
            String name,
            double maxScorePerMethod,
            Class<?> supertype
    ) {
        return makeOverrideChecker(name, maxScorePerMethod, supertype, 0, Modifier.FINAL | Modifier.STATIC);
    }

    /**
     * Creates an override checker that ensures that, if methods with the
     * same names as abstract methods in the supertype are found, they have the
     * {@link Override} annotation. All methods in interfaces are considered
     * abstract.
     * <p>
     * This currently checks only method names, not argument lists, and does not
     * consider methods declared in the supertype's supertypes.
     *
     * @param name              the name of the checker
     * @param maxScorePerMethod the per-method score
     * @param supertype         the class or interface containing the abstract
     *                          method declarations
     */
    public static OverrideChecker makeAllAbstractMethodChecker(
            String name,
            double maxScorePerMethod,
            Class<?> supertype
    ) {
        return makeOverrideChecker(
                name,
                maxScorePerMethod,
                supertype,
                Modifier.ABSTRACT,
                Modifier.FINAL
        );
    }

    @Override
    public double getTotalMaxScore() {
        return maxScorePerInstance * methodsToOverride.size();
    }

    private class OverrideCheckerAdapter extends VoidVisitorAdapter<List<Result>> {
        private final List<String> namesOfMethodsToOverride;

        private OverrideCheckerAdapter() {
            this.namesOfMethodsToOverride = methodsToOverride
                    .stream()
                    .map(Method::getName)
                    .toList();
        }

        @Override
        public void visit(MethodDeclaration methodDecl, List<Result> results) {
            String methodName = methodDecl.getNameAsString();
            if (namesOfMethodsToOverride.contains(methodName)) {
                if (methodDecl.getAnnotationByClass(Override.class).isPresent()) {
                    results.add(makeSuccessResult("Override annotation used correctly for " + methodName));
                } else {
                    results.add(makeFailingResult("Override annotation not used for " + methodName));
                }
            }
        }
    }
}
