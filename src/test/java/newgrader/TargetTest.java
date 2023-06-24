package newgrader;

import newgrader.common.Target;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TargetTest {
    @Test
    public void testSlashNormalization() {
        Target target1 = Target.fromRelativePathString("test/java/newgrader/TargetTest.java");
        Target target2 = Target.fromRelativePathString("test\\java\\newgrader\\TargetTest.java");
        assertEquals(target1.toPathString(), target2.toPathString());
    }
}
