package com.spertus.jacquard.junittester;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.spertus.jacquard.common.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.*;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

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
        return processResults(listener.results);
    }

    // Note that the merged result has the default visibility set when the autograder was constructed.
    @VisibleForTesting
    static Result mergeResults(List<Result> results) {
        Preconditions.checkArgument(!results.isEmpty());
        if (results.size() == 1) {
            return results.get(0);
        }
        String name = results.get(0).getName();
        Preconditions.checkArgument(
                results.stream().allMatch((r) -> r.getName().equals(name)));

        double score = results.stream().mapToDouble(Result::getScore).sum();
        double maxScore = results.stream().mapToDouble(Result::getMaxScore).sum();
        String message = results.stream()
                .filter((r) -> r.getScore() < r.getMaxScore())
                .map(r ->  String.format(Locale.US,
                                "%.1f: %s",
                                r.getScore() - r.getMaxScore(),
                                r.getMessage()))
                .collect(Collectors.joining("\n"));

        return Result.makeResult(name, score, maxScore, message);
    }

    // Merge all the results having the same name. The merged result has the
    // default visibility level specified when creating the autograder.
    private static List<Result> processResults(List<Result> results) {
        return results.stream()
                .collect(Collectors.groupingBy(Result::getName))
                .values()
                .stream()
                .map(JUnitTester::mergeResults)
                .collect(Collectors.toList());
    }

    private static class Listener implements TestExecutionListener { // NOPMD
        // ArrayList is used so we know it's mutable.
        private final ArrayList<Result> results = new ArrayList<>();
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

        private String makeMessage(final GradedTest gt, final TestExecutionResult teResult) {
            final List<String> items = new ArrayList<>();

            // First, use description, if present.
            if (!gt.description().isEmpty()) {
                items.add(gt.description());
            }

            // Second, use throwable, if present.
            teResult.getThrowable().ifPresent(value -> items.add(value.toString()));

            // Third, include output, if present and supposed to be shown.
            final String output = baos.toString().trim();
            if (gt.includeOutput() && !output.isEmpty()) {
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
                                        Result.makeSuccess(name, gt.points(), makeMessage(gt, testExecutionResult));
                                case FAILED, ABORTED ->
                                        Result.makeFailure(name, gt.points(), makeMessage(gt, testExecutionResult));
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
