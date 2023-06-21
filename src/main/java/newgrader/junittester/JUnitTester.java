package newgrader.junittester;

import newgrader.common.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

public class JUnitTester {
    private final List<? extends DiscoverySelector> selectors;
    private PrintStream stdoutCapture;

    public JUnitTester(Class<?> clazz) {
        selectors = List.of(selectClass(clazz));
    }

    public JUnitTester(Class<?>... classes) {
        selectors = Arrays.stream(classes)
                .map(DiscoverySelectors::selectClass)
                .toList();
    }

    public JUnitTester(String packageName) {
        selectors = List.of(selectPackage(packageName));
    }

    public List<Result> run() {
        Launcher launcher = LauncherFactory.create();
        JUnitTester.Listener listener = new Listener();
        launcher.registerTestExecutionListeners(listener);
        PrintStream originalOut = System.out;
        launcher.execute(request().selectors(selectors).build());
        System.setOut(originalOut);
        return listener.results;
    }

    private static class Listener implements TestExecutionListener {
        private final List<Result> results = new ArrayList<>();
        // These get set in executionStarted and used/closed in executionFinished.
        private PrintStream ps;
        private ByteArrayOutputStream baos;

        @Override
        public void executionStarted(TestIdentifier testIdentifier) {
            baos = new ByteArrayOutputStream();
            if (ps != null) {
                ps.close();
            }
            ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
            System.setOut(ps);
        }

        private String makeOutput(TestExecutionResult teResult) {
            Optional<Throwable> throwable = teResult.getThrowable();
            String s = baos.toString();
            if (throwable.isEmpty()) {
                return s;
            }
            else if (s.isEmpty()) {
                return throwable.get().toString();
            }
            else {
                return s + "\n" + throwable.get();
            }
        }

        @Override
        public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
            if (testIdentifier.getSource().isPresent()) {
                TestSource source = testIdentifier.getSource().get();
                if (source instanceof MethodSource methodSource) {
                    GradedTest gt = methodSource.getJavaMethod().getAnnotation(GradedTest.class);
                    if (gt != null) {
                        try {
                            results.add(switch (testExecutionResult.getStatus()) {
                                case SUCCESSFUL ->
                                        Result.makeSuccess(gt.name(), gt.points(), baos.toString());
                                case FAILED, ABORTED ->
                                        Result.makeTotalFailure(gt.name(), gt.points(), makeOutput(testExecutionResult));
                            });
                            ps.close();
                        } catch (NoSuchElementException e) { // if get() failed
                            results.add(Result.makeTotalFailure(gt.name(), gt.points(), "Test failed with no additional information"));
                        }
                    }
                }
            }
        }
    }
}
