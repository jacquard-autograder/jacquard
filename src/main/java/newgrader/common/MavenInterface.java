package newgrader.common;

import newgrader.exceptions.*;

import java.io.IOException;
import java.util.*;

public class MavenInterface {
    private static final String PATH_TO_MAVEN_WINDOWS = "C:/Program Files/apache-maven-3.6.3/bin/mvn.cmd";
    private static final String PATH_TO_MAVEN_LINUX = "mvn";

    private MavenInterface() {}

    public static String getPathToMaven() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return PATH_TO_MAVEN_WINDOWS;
        } else {
            return PATH_TO_MAVEN_LINUX;
        }
    }

    public static void runMavenProcess(List<String> args) throws DependencyException {
        List<String> command = new ArrayList<>(args.size() + 1);
        command.add(MavenInterface.getPathToMaven());
        command.addAll(args);
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();
        try {
            Process p = pb.start();
            int result = p.waitFor();
            if (result != 0) {
                throw new DependencyException("Non-zero exit code when running maven with this command line: " + command);
            }
        } catch (AutograderException | IOException | InterruptedException e) {
            throw new DependencyException("Error running maven ", e);
        }
    }
}
