package com.spertus.jacquard;

import com.spertus.jacquard.common.Target;
import com.spertus.jacquard.exceptions.ClientException;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.nio.file.*;

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
    public void testToClassName() {
        assertEquals("Mob", mobTarget.toClassName());
    }

    @Test
    public void testToClassNameThrowsExceptionForNonJava() {
        assertThrows(ClientException.class,
                () -> TestUtilities.getTargetFromResource("invalid/NotJava.txt").toClassName());
    }

    @Test
    public void testToClassNameThrowsExceptionForDirectory() {
        assertThrows(ClientException.class,
                () -> Target.fromPathString(".").toClassName());
    }

    @Test
    public void testToClassNameWorksForUnparseable() throws URISyntaxException {
        assertEquals("Unparseable", TestUtilities.getTargetFromResource("invalid/Unparseable.java").toClassName());
    }

    @Test
    public void testToPackageName() {
        assertEquals("student", mobTarget.toPackageName());
    }

    @Test
    public void testToPackageNameThrowsExceptionForNonJava() {
        assertThrows(ClientException.class,
                () -> TestUtilities.getTargetFromResource("invalid/NotJava.txt").toPackageName());
    }

    @Test
    public void testToPackageNameThrowsExceptionForDirectory() {
        assertThrows(ClientException.class,
                () -> Target.fromPathString(".").toPackageName());
    }

    @Test
    public void testToPackageNameThrowsExceptionForUnparseable() {
        assertThrows(ClientException.class,
                () -> TestUtilities.getTargetFromResource("invalid/Unparseable.java").toPackageName());
    }

}
