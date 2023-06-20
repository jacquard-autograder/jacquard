package newgrader.junittester;

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

public class JUnitTester {
    private final List<? extends DiscoverySelector> selectors;

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
        launcher.execute(request().selectors(selectors).build());
        return listener.results;
    }

    private static class Listener implements TestExecutionListener {
        private final List<Result> results = new ArrayList<>();

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
}
