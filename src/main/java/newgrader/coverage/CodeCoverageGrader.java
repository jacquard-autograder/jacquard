package newgrader.coverage;

import org.jacoco.core.analysis.*;
import org.jacoco.core.data.*;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.*;

import java.io.InputStream;

public class CodeCoverageGrader {
    private static String CLASS_UNDER_TEST = "newgrader.coverage.PrimeChecker";
    private static String TEST_CLASS = "newgrader.coverage.PrimeCheckerTest";

    public void execute() throws Exception {
        final String targetName = CLASS_UNDER_TEST;

        // For instrumentation and runtime we need a IRuntime instance
        // to collect execution data:
        final IRuntime runtime = new LoggerRuntime();

        // The Instrumenter creates a modified version of our test target class
        // that contains additional probes for execution data recording:
        final Instrumenter instr = new Instrumenter(runtime);
        InputStream original = getTargetClass(targetName);
        final byte[] instrumented = instr.instrument(original, targetName);
        original.close();

        // Now we're ready to run our instrumented class and need to startup the
        // runtime first:
        final RuntimeData data = new RuntimeData();
        runtime.startup(data);

        // In this tutorial we use a special class loader to directly load the
        // instrumented class definition from a byte[] instances.
        final MemoryClassLoader memoryClassLoader = new MemoryClassLoader();
        memoryClassLoader.addDefinition(targetName, instrumented);
        final Class<?> targetClass = memoryClassLoader.loadClass(targetName);

        memoryClassLoader.addDefinition(TEST_CLASS, instrumented);
      //  final Class<? extends Runnable> testClass = (Class<? extends Runnable>) memoryClassLoader.loadClass(getTargetClass(TEST_CLASS);

        // Here we execute our test target class through its Runnable interface:
      //  final Runnable testInstance = testClass.newInstance();
     //   testInstance.run();

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
        original = getTargetClass(targetName);
        analyzer.analyzeClass(original, targetName);
        original.close();

        // Let's dump some metrics and line coverage information:
        for (IClassCoverage cc : coverageBuilder.getClasses()) {
            System.out.println(cc);
        }
    }

    private InputStream getTargetClass(final String name) {
        final String resource = '/' + name.replace('.', '/') + ".class";
        return getClass().getResourceAsStream(resource);
    }

    public static void main(String[] args) throws Exception {
        new CodeCoverageGrader().execute();
    }
}
