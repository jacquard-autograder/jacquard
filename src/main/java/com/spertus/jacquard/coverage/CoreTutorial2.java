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

import java.io.InputStream;
import java.io.PrintStream;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;

/**
 * Example usage of the JaCoCo core API. In this tutorial a single target class
 * will be instrumented and executed. Finally the coverage information will be
 * dumped.
 */
public final class CoreTutorial2 {
    /**
     * The class with the tests of the target class.
     */
    public static class TestClass implements Runnable {
        @Override
        public void run() {
            new ClassUnderTest().isPrime(7);
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
    public CoreTutorial2(final PrintStream out) {
        this.out = out;
    }

    private void runTests(Class<?> testClass) throws InstantiationException, IllegalAccessException {
        final Runnable testInstance = (Runnable) testClass.newInstance();
        testInstance.run();
    }

    /**
     * Run this example.
     *
     * @throws Exception
     *             in case of errors
     */
    public void execute(final String cutName, final String testClassName) throws Exception {
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

        // Here we execute our test class through its Runnable interface:
        final Class<?> testClass = memoryClassLoader.loadClass(testClassName);
        final Runnable testInstance = (Runnable) testClass.newInstance();
        testInstance.run();

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
        new CoreTutorial2(System.out).execute(ClassUnderTest.class.getName(), TestClass.class.getName());
    }

}
