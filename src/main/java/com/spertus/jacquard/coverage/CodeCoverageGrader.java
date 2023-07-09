package com.spertus.jacquard.coverage;

import com.spertus.jacquard.common.*;
import com.spertus.jacquard.exceptions.*;
import org.jacoco.core.analysis.*;
import org.jacoco.core.data.*;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.*;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.core.LauncherFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

/**
 * A grader that uses Jacoco to measure code coverage of tests.
 */
public class CodeCoverageGrader extends Grader {
    private static final String GRADER_NAME = "Code Coverage Grader";
    private static final String PATH_TO_JACOCO_POM = "pom-jacoco.xml";
    private static final List<String> JACOCO_COMMAND_LINE_ARGS = List.of(
            "-f",
            PATH_TO_JACOCO_POM,
            "clean",
            "verify");

    private static final String PATH_TO_JACOCO_CSV = "target/site/jacoco/jacoco.csv";
    // Jacoco CSV file
    private static final int PACKAGE_FIELD = 1;
    private static final int CLASS_FIELD = 2;
    private static final int NUM_FIELDS = 13;

    private final Scorer scorer;

    /**
     * Creates a code coverage grader with the given name and scorer.
     *
     * @param name   the name
     * @param scorer a scorer, which converts the outcome to a point value
     */
    public CodeCoverageGrader(String name, Scorer scorer) {
        super(name);
        this.scorer = scorer;
    }

    /**
     * Creates a code coverage grader with the given scorer.
     *
     * @param scorer a scorer, which converts the outcome to a point value
     */
    public CodeCoverageGrader(Scorer scorer) {
        this(GRADER_NAME, scorer);
    }

    private ClassInfo getClassInfo(Target target) throws AutograderException {
        try {
            Path path = Paths.get(PATH_TO_JACOCO_CSV);
            String packageName = target.toPackageName();
            String className = target.toClassName();

            // It is not really necessary to read in all lines,
            // so performance could be improved here.
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] fields = line.split(",");
                if (fields.length == NUM_FIELDS) {
                    if (fields[PACKAGE_FIELD].equals(packageName) && fields[CLASS_FIELD].equals(className)) {
                        return new ClassInfo(fields);
                    }
                }
            }
        } catch (IOException e) {
            throw new DependencyException("Jacoco output not found with base dir " + System.getProperty("user.dir"), e);
        }
        throw new ClientException(
                String.format("No class info found for %s", target.toPathString().toString()));
    }

    private static void runTestClasses(ClassLoader classLoader, Class<?>... testClasses) {
        final List<? extends DiscoverySelector> selectors =
                Arrays.stream(testClasses)
                        .map(DiscoverySelectors::selectClass)
                        .toList();
        final Launcher launcher = LauncherFactory.create();
        // TODO: Swallow input.
        CustomContextClassLoaderExecutor executor = new CustomContextClassLoaderExecutor(Optional.ofNullable(classLoader));
        executor.invoke(() -> executeTests(selectors));
    }

    private static int executeTests(List<? extends DiscoverySelector> selectors) {
        Launcher launcher = LauncherFactory.create();
        launcher.execute(request().selectors(selectors).build());
        return 0;
    }

    // This code is based on
    // https://www.jacoco.org/jacoco/trunk/doc/examples/java/CoreTutorial.java
    // by Marc R. Hoffmann and is
    // Copyright (c) 2009, 2023 Mountainminds GmbH & Co. KG and Contributors
    // and made available under
    // the terms of the Eclipse Public License 2.0 which is available at
    // http://www.eclipse.org/legal/epl-2.0
    public static void runJacoco(String targetClassName, String targetPathString, Class<?>... testClasses) throws Exception {
        // For instrumentation and runtime we need a IRuntime instance
        // to collect execution data:
        final IRuntime runtime = new LoggerRuntime();

        // The Instrumenter creates a modified version of our test target class
        // that contains additional probes for execution data recording:
        final Instrumenter instr = new Instrumenter(runtime);
        InputStream original = getTargetClass(targetClassName);
        final byte[] instrumented = instr.instrument(original, targetClassName);
        original.close();

        // Now we're ready to run our instrumented class and need to startup the
        // runtime first:
        final RuntimeData data = new RuntimeData();
        runtime.startup(data);

        // In this tutorial we use a special class loader to directly load the
        // instrumented class definition from a byte[] instances.
        final MemoryClassLoader memoryClassLoader = new MemoryClassLoader();
        memoryClassLoader.addDefinition(targetClassName, instrumented);
 //       final Class<?> targetClass = memoryClassLoader.loadClass(targetClassName);

        // Run test class.
        // TODO: Make it use the instrumented class, not the original.
        runTestClasses(memoryClassLoader, testClasses);
        // https://stackoverflow.com/a/41394027/631051

        // At the end of test execution we collect execution data and shutdown
        // the runtime:
        final ExecutionDataStore executionData = new ExecutionDataStore();
        final SessionInfoStore sessionInfos = new SessionInfoStore();
        data.collect(executionData, sessionInfos, false);
        runtime.shutdown();

        // Together with the original class definition we can calculate coverage
        // information:
        final CoverageBuilder coverageBuilder = new CoverageBuilder();
        final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);
        original = getTargetClass(targetClassName);
        analyzer.analyzeClass(original, targetClassName);
        original.close();

        // Let's dump some metrics and line coverage information:
        for (final IClassCoverage cc : coverageBuilder.getClasses()) {
            // TODO: Extract data.
            System.out.println(cc);
        }
    }

    private static InputStream getTargetClass(final String name) {
        final String resource = '/' + name.replace('.', '/') + ".class";
        return CodeCoverageGrader.class.getResourceAsStream(resource);
    }

    @Override
    public List<Result> grade(Target target) {

        try {
            List<String> args = new ArrayList<>(JACOCO_COMMAND_LINE_ARGS);
            args.add("-Dstudent.srcdir=" + target.toDirectory());
            MavenInterface.runMavenProcess(args);
            ClassInfo classInfo = getClassInfo(target);
            return List.of(scorer.getResult(classInfo));
        } catch (DependencyException e) {
            return makeExceptionResultList(
                    new InternalException(
                            "Exception was thrown when running autograder", e));
        }
    }
}
