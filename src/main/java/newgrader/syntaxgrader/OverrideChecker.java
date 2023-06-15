package newgrader.syntaxgrader;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import newgrader.common.Result;
import newgrader.exceptions.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * A checker that ensures that the {@link Override} annotation is used
 * when overriding specified methods.
 */
public final class OverrideChecker extends SyntaxChecker {
    private final List<Method> methodsToOverride;
    // Methods will be removed from this list as they are found.
    private List<Method> remainingMethodsToOverride;

    // Throws ClientException if any of the methods are final or static.
    private OverrideChecker(String name, double maxScorePerMethod, List<Method> methodsToOverride)
            throws ClientException {
        // We can't create the adapter until the superclass constructor has completed.
        super(name, maxScorePerMethod, null);

        // Check that no methods are static or final.
        for (final Method method : methodsToOverride) {
            if ((method.getModifiers() & Modifier.FINAL) != 0) {
                throw new ClientException(
                        String.format(
                                "Method %s is final and cannot be overridden.", method.getName()));
            }
            if ((method.getModifiers() & Modifier.STATIC) != 0) {
                throw new ClientException(
                        String.format(
                                "Method %s is static and cannot be overridden.", method.getName()));
            }
        }
        this.methodsToOverride = new ArrayList<>(methodsToOverride); // defensive copy

        // Now we can create and initialize the adapter.
        adapter = new OverrideCheckerAdapter();
    }

    /**
     * Creates an override checker that ensures that specified methods have the
     * {@link Override} annotation.
     *
     * @param name              the name of the checker
     * @param maxScorePerMethod the per-method score
     * @param methodsToOverride methods to override
     * @return a new override checker
     * @throws ClientException if any of the methods are static or final (cannot be overridden)
     */
    public static OverrideChecker makeOverrideCheckerFromMethodList(
            String name,
            double maxScorePerMethod,
            List<Method> methodsToOverride
    ) throws ClientException {
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
    ) throws ClientException {
        final Method[] allMethods = supertype.isInterface() ? supertype.getMethods() : supertype.getDeclaredMethods();
        final List<Method> methods = Arrays.stream(allMethods)
                .filter(method -> (method.getModifiers() & requiredModifiers) == requiredModifiers
                        && (method.getModifiers() & forbiddenModifiers) == 0)
                .toList();
        return new OverrideChecker(name, maxScorePerMethod, methods);
    }

    /**
     * Creates an override checker that ensures that, if methods declared in
     * the supertype are found in the actual code, they have the {@link Override}
     * annotation. Final and static methods are skipped. This does not consider
     * methods declared in the supertype's supertypes.
     *
     * @param name              the name of the checker
     * @param maxScorePerMethod the per-method score
     * @param supertype         the class or interface containing the method declarations
     * @return a new override checker
     */
    public static OverrideChecker makeAllAllowableMethodChecker(
            String name,
            double maxScorePerMethod,
            Class<?> supertype
    ) {
        try {
            return makeOverrideChecker(name, maxScorePerMethod, supertype, 0, Modifier.FINAL | Modifier.STATIC);
        } catch (ClientException e) {
            // Should not happen because all methods are allowable
            throw new InternalException(e);
        }
    }

    /**
     * Creates an override checker that ensures that, if methods declared abstract
     * in the supertype are found, they have the {@link Override} annotation.
     * All methods in interfaces are considered abstract.
     *
     * @param name              the name of the checker
     * @param maxScorePerMethod the per-method score
     * @param supertype         the class or interface containing the abstract
     *                          method declarations
     * @return a new override checker
     */
    public static OverrideChecker makeAllAbstractMethodChecker(
            String name,
            double maxScorePerMethod,
            Class<?> supertype
    ) {
        try {
            return makeOverrideChecker(
                    name,
                    maxScorePerMethod,
                    supertype,
                    Modifier.ABSTRACT,
                    Modifier.FINAL
            );
        } catch (ClientException e) {
            // Should not happen because all methods are abstract
            throw new InternalException(e);
        }
    }

    @Override
    public double getTotalMaxScore() {
        return maxScorePerInstance * methodsToOverride.size();
    }

    @Override
    public void initialize() {
        remainingMethodsToOverride = new ArrayList<>(methodsToOverride);
    }

    @Override
    public void finalizeResults(List<Result> results) {
        for (final Method method : remainingMethodsToOverride) {
            results.add(makeFailingResult(String.format("Expected method '%s' not found", method.getName())));
        }
    }

    private class OverrideCheckerAdapter extends VoidVisitorAdapter<List<Result>> {
        private boolean parameterListsEquivalent(MethodDeclaration methodDecl, Method method) {
            if (methodDecl.getParameters().size() != method.getParameterTypes().length) {
                return false;
            }
            for (int i = 0; i < methodDecl.getParameters().size(); i++) {
                if (!methodDecl.getParameter(i).getType().asString().equals(
                        method.getParameterTypes()[i].getSimpleName())) {
                    return false;
                }
            }
            return true;
        }

        private Optional<Method> getMatchingMethod(MethodDeclaration methodDecl) {
            return methodsToOverride.stream().filter((Method m) ->
                            m.getName().equals(methodDecl.getNameAsString())
                                    && parameterListsEquivalent(methodDecl, m))
                    .findFirst();
        }

        @Override
        public void visit(MethodDeclaration methodDecl, List<Result> results) {
            final Optional<Method> expectedMethod = getMatchingMethod(methodDecl);
            if (expectedMethod.isPresent()) {
                final String methodName = methodDecl.getNameAsString();
                if (methodDecl.getAnnotationByClass(Override.class).isPresent()) {
                    results.add(makeSuccessResult("Override annotation used correctly for " + methodName));
                } else {
                    results.add(makeFailingResult("Override annotation not used for " + methodName));
                }
                remainingMethodsToOverride.remove(expectedMethod.get());
            }
        }
    }
}
