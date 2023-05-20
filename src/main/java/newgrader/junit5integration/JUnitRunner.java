package newgrader.junit5integration;

import client.staff.ArrayFlistTest;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherFactory;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

public class JUnitRunner {
    public static void main(String[] args) {
        Launcher launcher = LauncherFactory.create();

        TestExecutionListener listener = new TestExecutionListener() {
            @Override
            public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
                System.out.println("Test finished: " + testIdentifier.getDisplayName());
                System.out.println("Status: " + testExecutionResult.getStatus());
            }
        };

        launcher.registerTestExecutionListeners(listener);

        launcher.execute(request().selectors(selectClass(ArrayFlistTest.class)).build());
    }
}
