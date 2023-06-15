package newgrader.junit5integration;

import newgrader.common.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherFactory;

import java.util.*;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

public class JUnitRunner {

    public List<Result> runTest(Class<?> clazz) {
        final List<Result> results = new ArrayList<>();
        Launcher launcher = LauncherFactory.create();

        TestExecutionListener listener = new TestExecutionListener() {
            @Override
            public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
                if (testIdentifier.getSource().isPresent()) {
                    TestSource source = testIdentifier.getSource().get();
                    if (source instanceof MethodSource methodSource) {
                        GradedTest gt = methodSource.getJavaMethod().getAnnotation(GradedTest.class);
                        if (gt != null) {
                            try {
                                results.add(switch (testExecutionResult.getStatus()) {
                                    case SUCCESSFUL -> Result.makeSuccess(gt.name(), gt.points(), "SUCCESS");
                                    case FAILED -> Result.makeTotalFailure(gt.name(), gt.points(), testExecutionResult.getThrowable().get().toString());
                                    case ABORTED -> Result.makeTotalFailure(gt.name(), gt.points(), testExecutionResult.getThrowable().get().getMessage());
                                });
                            } catch (NoSuchElementException e) { // if get() failed
                                results.add(Result.makeTotalFailure(gt.name(), gt.points(), "Test failed with no additional information"));
                            }
                        }
                    }
                }
            }
        };

        launcher.registerTestExecutionListeners(listener);

        launcher.execute(request().selectors(selectClass(clazz)).build());
        return results;
    }
}
