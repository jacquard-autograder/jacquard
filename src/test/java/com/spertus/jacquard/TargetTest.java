package com.spertus.jacquard;

import com.spertus.jacquard.common.Target;
import com.spertus.jacquard.exceptions.ClientException;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TargetTest {
    private static final String GOOD_RESOURCES_SUBDIR = "build/resources/test/good";
    private static final String MOB_PATH_STRING = GOOD_RESOURCES_SUBDIR + "/Mob.java";
    private Target mobTarget; // created from resource

    @BeforeEach
    public void setup() throws URISyntaxException {
        mobTarget = TestUtilities.getTargetFromResource("good/Mob.java");
    }

    @Test
    public void testSlashNormalization() {
        Target target1 = Target.fromPathString("test/java/newgrader/TargetTest.java");
        Target target2 = Target.fromPathString("test\\java\\newgrader\\TargetTest.java");
        assertEquals(target1.toPathString(), target2.toPathString());
    }

    @Test
    public void testDotNormalization() {
        Target target1 = Target.fromPathString("test/java/newgrader/TargetTest.java");
        Target target2 = Target.fromPathString("test/java/newgrader/../newgrader/TargetTest.java");
        assertEquals(target1, target2);
        assertEquals(target1.toPathString(), target2.toPathString());
    }

    @Test
    public void testToPathForResource() {
        Path path = mobTarget.toPath();
        Path expectedPath = Paths.get(MOB_PATH_STRING).toAbsolutePath();
        assertEquals(expectedPath, path);
    }

    @Test
    public void testToPathStringForResource() {
        Path expectedPath = Paths.get(MOB_PATH_STRING).toAbsolutePath();
        assertEquals(expectedPath.toString(), mobTarget.toPathString());
    }

    @Test
    public void testToFileForResource() {
        Path expectedPath = Paths.get(MOB_PATH_STRING).toAbsolutePath();
        assertEquals(expectedPath.toString(), mobTarget.toPathString());
    }

    @Test
    public void testToDirectoryForResource() {
        assertEquals(Paths.get(GOOD_RESOURCES_SUBDIR).toAbsolutePath(), mobTarget.toDirectory());
    }

    @Test
    public void testFromDirectoryReturnsAllFiles() {
        List<Target> targets = Target.fromDirectory("src/test/resources/good/");
        assertEquals(12, targets.size());
    }

    @Test
    public void testFromDirectoryRejectsFilePath() {
        assertThrows(ClientException.class,
                () -> Target.fromDirectory("src/test/resources/good/BadFormatting.java"));
    }

    @Test
    public void testFromDirectoryRejectsBadPath() {
        assertThrows(ClientException.class,
                () -> Target.fromDirectory("src/test/resources/nosuchdir"));
    }
}