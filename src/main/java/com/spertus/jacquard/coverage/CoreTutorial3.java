// This version of CoreTutorial separates the class under test (ClassUnderTest)
// from the test class (TestClass) that implements Runnable.
// No data is collected.

package com.spertus.jacquard.coverage;

/*******************************************************************************
 * Copyright (c) 2009, 2023 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 *******************************************************************************/

import org.jacoco.core.analysis.*;
import org.jacoco.core.data.*;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.*;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.core.LauncherFactory;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

/**
 * Example usage of the JaCoCo core API. In this tutorial a single target class
 * will be instrumented and executed. Finally the coverage information will be
 * dumped.
 */
public final class CoreTutorial3 {
    /**
     * The class with the tests of the target class.
     */
    public static class TestClass {
        @Test
        public void run() {
            ClassUnderTest primeFinder = new ClassUnderTest();
            assertTrue(primeFinder.isPrime(7));
        }
    }

    /**
     * The test target we want to see code coverage for.
     */
    public static class ClassUnderTest  {
        public boolean isPrime(final int n) {
            for (int i = 2; i * i <= n; i++) {
                if ((n ^ i) == 0) {
                    return false;
                }
            }
            return true;
        }
    }

    private final PrintStream out;

    /**
     * Creates a new example instance printing to the given stream.
     *
     * @param out
     *            stream for outputs
     */
    public CoreTutorial3(final PrintStream out) {
        this.out = out;
    }

    private void runTests(Class<?> testClass) throws InstantiationException, IllegalAccessException {
        final Runnable testInstance = (Runnable) testClass.newInstance();
        testInstance.run();
    }

    private void runJUnitTests(MemoryClassLoader memoryClassLoader, Class<?>... testClasses) {
        final List<? extends DiscoverySelector> selectors =
                Arrays.stream(testClasses)
                        .map(DiscoverySelectors::selectClass)
                        .toList();
        CustomContextClassLoaderExecutor executor = new CustomContextClassLoaderExecutor(Optional.ofNullable(memoryClassLoader));
        executor.invoke(() -> executeTests(selectors));
    }

    private static int executeTests(List<? extends DiscoverySelector> selectors) {
        Launcher launcher = LauncherFactory.create();
        launcher.execute(request().selectors(selectors).build());
        return 0;
    }

    /**
     * Run this example.
     *
     * @throws Exception
     *             in case of errors
     */
    public void execute(final Class<?> classUnderTest, Class<?> testClass) throws Exception {
        final String cutName = classUnderTest.getName();
        final String testClassName = testClass.getName();

        final IRuntime runtime = new LoggerRuntime();
        final Instrumenter instrumenter = new Instrumenter(runtime);
        final MemoryClassLoader memoryClassLoader = new MemoryClassLoader();

        // Instrument classes and add to class loader.
        try (InputStream is = getTargetClass(cutName)) {
            byte[] instrumented = instrumenter.instrument(is, cutName);
            memoryClassLoader.addDefinition(cutName, instrumented);
        }
        try (InputStream is = getTargetClass(testClassName)) {
            byte[] instrumented = instrumenter.instrument(is, testClassName);
            memoryClassLoader.addDefinition(testClassName, instrumented);
        }

        // Now we're ready to run our instrumented class and need to startup the
        // runtime first:
        final RuntimeData data = new RuntimeData();
        runtime.startup(data);

        final Class<?> instrumentedTestClass = memoryClassLoader.loadClass(testClassName);
        //runTests(testClass);
        runJUnitTests(memoryClassLoader, instrumentedTestClass);

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
        try (InputStream is = getTargetClass(cutName)) {
            analyzer.analyzeClass(is, cutName);
        }

        // Let's dump some metrics and line coverage information:
        for (final IClassCoverage cc : coverageBuilder.getClasses()) {
            out.printf("Coverage of class %s%n", cc.getName());

            printCounter("instructions", cc.getInstructionCounter());
            printCounter("branches", cc.getBranchCounter());
            printCounter("lines", cc.getLineCounter());
            printCounter("methods", cc.getMethodCounter());
            printCounter("complexity", cc.getComplexityCounter());

            for (int i = cc.getFirstLine(); i <= cc.getLastLine(); i++) {
                out.printf("Line %s: %s%n", Integer.valueOf(i),
                        getColor(cc.getLine(i).getStatus()));
            }
        }
    }

    private InputStream getTargetClass(final String name) {
        final String resource = '/' + name.replace('.', '/') + ".class";
        return getClass().getResourceAsStream(resource);
    }

    private void printCounter(final String unit, final ICounter counter) {
        final Integer missed = Integer.valueOf(counter.getMissedCount());
        final Integer total = Integer.valueOf(counter.getTotalCount());
        out.printf("%s of %s %s missed%n", missed, total, unit);
    }

    private String getColor(final int status) {
        switch (status) {
            case ICounter.NOT_COVERED:
                return "red";
            case ICounter.PARTLY_COVERED:
                return "yellow";
            case ICounter.FULLY_COVERED:
                return "green";
        }
        return "";
    }

    /**
     * Entry point to run this examples as a Java application.
     *
     * @param args
     *            list of program arguments
     * @throws Exception
     *             in case of errors
     */
    public static void main(final String[] args) throws Exception {
        new CoreTutorial3(System.out).execute(ClassUnderTest.class, TestClass.class);
    }

}
