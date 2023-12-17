package com.spertus.jacquard.junittester;

import com.spertus.jacquard.common.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.*;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

/**
 * A tester that runs JUnit tests having the {@link GradedTest} annotation.
 */
public class JUnitTester extends Tester {
    private final List<? extends DiscoverySelector> selectors;
    private final DiscoveryFilter<String> filter;

    /**
     * Constructs a JUnit tester that will run tests in the specified classes.
     *
     * @param classes the classes containing the tests
     */
    public JUnitTester(final Class<?>... classes) {
        super();
        selectors = Arrays.stream(classes)
                .map(DiscoverySelectors::selectClass)
                .toList();
        filter = null;
    }

    /**
     * Constructs a JUnit tester that will run tests in the specified package
     * and (optionally) its subpackages.
     *
     * @param packageName        the package containing the tests
     * @param includeSubpackages whether to include tests in subpackages of the
     *                           package
     */
    public JUnitTester(final String packageName, final boolean includeSubpackages) {
        super();
        selectors = List.of(selectPackage(packageName));
        filter = includeSubpackages ? null :
                ClassNameFilter.excludeClassNamePatterns(
                        packageName + "\\.[^.]+\\..*");
    }

    @Override
    public List<Result> run() {
        final Launcher launcher = LauncherFactory.create();
        final JUnitTester.Listener listener = new Listener();
        launcher.registerTestExecutionListeners(listener);
        final PrintStream originalOut = System.out; // NOPMD
        LauncherDiscoveryRequestBuilder builder = request().selectors(selectors);
        if (filter != null) {
            builder = builder.filters(filter);
        }
        launcher.execute(builder.build());
        System.setOut(originalOut);
        return listener.results;
    }

    private static class Listener implements TestExecutionListener { // NOPMD
        private final List<Result> results = new ArrayList<>();
        // These get set in executionStarted and used/closed in executionFinished.
        private PrintStream ps;
        private ByteArrayOutputStream baos;

        @Override
        public void executionStarted(final TestIdentifier testIdentifier) {
            baos = new ByteArrayOutputStream();
            if (ps != null) {
                ps.close();
            }
            ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
            System.setOut(ps);
        }

        private String makeMessage(final String description, final TestExecutionResult teResult) {
            final List<String> items = new ArrayList<>();

            // First, use description, if present.
            if (!description.isEmpty()) {
                items.add(description);
            }

            // Second, use throwable, if present.
            teResult.getThrowable().ifPresent(value -> items.add(value.toString()));

            // Third, include output, if present.
            final String output = baos.toString();
            if (!output.isEmpty()) {
                items.add("OUTPUT");
                items.add("======");
                items.add(output);
            }
            return String.join("\n", items);
        }

        @Override
        public void executionFinished(
                final TestIdentifier testIdentifier,
                final TestExecutionResult testExecutionResult) {
            if (testIdentifier.getSource().isPresent()) {
                final TestSource source = testIdentifier.getSource().get();
                if (source instanceof MethodSource methodSource) {
                    final GradedTest gt = methodSource.getJavaMethod().getAnnotation(GradedTest.class);
                    if (gt != null) {
                        final String name = gt.name().isEmpty() ? testIdentifier.getDisplayName() : gt.name();
                        try {
                            final Result result = switch (testExecutionResult.getStatus()) {
                                case SUCCESSFUL ->
                                        Result.makeSuccess(name, gt.points(), baos.toString());
                                case FAILED, ABORTED ->
                                        Result.makeFailure(name, gt.points(), makeMessage(gt.description(), testExecutionResult));
                            };
                            results.add(result.changeVisibility(gt.visibility()));
                            ps.close();
                        } catch (NoSuchElementException e) { // if get() failed
                            results.add(
                                    Result.makeFailure(
                                                    name,
                                                    gt.points(),
                                                    "Test failed with no additional information")
                                            .changeVisibility(gt.visibility()));
                        }
                    }
                }
            }
        }
    }
}
