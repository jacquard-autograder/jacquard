package newgrader.common;

import java.io.File;
import java.nio.file.*;

public class StudentPathStringTarget extends PathStringTarget {
    // In the future, this may be different on client/server (Gradescope, etc.).
    public static final Path STUDENT_SRC_ROOT = Paths.get("src", "submission", "java");

    protected StudentPathStringTarget(String pathString) {
        super(STUDENT_SRC_ROOT + File.separator + pathString);
    }
}
