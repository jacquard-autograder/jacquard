package newgrader.junit5integration;

import newgrader.common.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherFactory;

import java.util.*;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

// not threadsafe
public class JUnitRunner {
    List<Result> results;

    public JUnitRunner() {

    }

    private class Listener implements TestExecutionListener {
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
                                        Result.makeSuccess(gt.name(), gt.points(), "SUCCESS");
                                case FAILED ->
                                        Result.makeTotalFailure(gt.name(), gt.points(), testExecutionResult.getThrowable().get().toString());
                                case ABORTED ->
                                        Result.makeTotalFailure(gt.name(), gt.points(), testExecutionResult.getThrowable().get().getMessage());
                            });
                        } catch (NoSuchElementException e) { // if get() failed
                            results.add(Result.makeTotalFailure(gt.name(), gt.points(), "Test failed with no additional information"));
                        }
                    }
                }
            }
        }
    }

    private List<Result> run(List<? extends DiscoverySelector> sel) {
        results = new ArrayList<>();
        Launcher launcher = LauncherFactory.create();
        TestExecutionListener listener = new Listener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request().selectors(sel).build());
        return results;
    }

    public List<Result> runTestClass(Class<?> clazz) {
        return run(List.of(selectClass(clazz)));
    }

    public List<Result> runTestClasses(Class<?>... classes) {
        List<? extends DiscoverySelector> classSelectors = Arrays.stream(classes)
                .map(DiscoverySelectors::selectClass)
                .toList();
        return run(classSelectors);
    }

    public List<Result> runTestPackage(String packageName) {
        return run(List.of(selectPackage(packageName)));
    }
}
