package student;

import newgrader.common.GradedTest;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.regex.*;

import static org.junit.jupiter.api.Assertions.*;

public class MobTest {
    private static String source = null;

    @BeforeAll
    public static void setup() throws IOException {
        source = "public class Mob {}";
        /*
        InputStream inputStream = MobTest.class.getResourceAsStream("../student/Mob.java");
        assertNotNull(inputStream);
        source = readFromInputStream(inputStream);

         */
    }

    @Test
    @GradedTest(name = "Mob getters work", points = 5.0, includeOutput = false)
    public void testMobGetters() {
        Mob mob = new Mob("squid", 100, Mob.Behavior.Hostile, 1, 10);
        assertEquals("squid", mob.getType());
        assertEquals(100, mob.getMaxHearts());
        assertEquals(Mob.Behavior.Hostile, mob.getBehavior());
        assertEquals(Mob.Status.Healthy, mob.getStatus());
        assertEquals(1, mob.getMinDamage());
        assertEquals(10, mob.getMaxDamage());
        assertEquals(100, mob.getNumHearts());
        mob.takeDamage(1);
        assertEquals(Mob.Status.Injured, mob.getStatus());
    }

    @Test
    @GradedTest(name = "Ternary operator used 3 times", points = 3.0)
    public void ternaryOperatorUsed() {
        // https://stackoverflow.com/a/60994000/631051
        // Replace first plus below with a star.
        Pattern ternaryMatchPattern = Pattern.compile("^(?!.*//).*[?]", Pattern.MULTILINE);
        Matcher ternaryMatcher = ternaryMatchPattern.matcher(source);
        long count = ternaryMatcher.results().count();
        assertNotEquals(0, count, "Did not use ternary operator");
        assertNotEquals(1, count, "Used ternary operator only once");
        assertNotEquals(2, count, "Used ternary operator only twice");
    }

    @Test
    @GradedTest(name = "Switch statement used", points = 5.0)
    public void switchStatementUsed() {
        // Don't attempt to tell if it is commented out.
        assertTrue(source.contains("switch"), "Switch statement not found");
    }

    @Test
    @GradedTest(name = "printf() or String.format() used", points = 5.0)
    public void testStringFormatting() {
        assertTrue(source.contains("printf") || source.contains("String.format("));
        assertTrue(source.contains("%s") || source.contains("%d")
                || source.contains("%1"));
    }

    @Test
    @GradedTest(name = "Provided javadoc used", points = 5.0)
    public void testJavadocPresent() {
        assertTrue(source.contains("#numHearts"));
        assertTrue(source.contains("@param victim"));
        assertTrue(source.contains("@param opponent"));
        assertTrue(source.contains("@throws AssertionError"));
    }

    private static final String[] privateFieldNames = {"type", "maxHearts", "behavior", "minDamage", "maxDamage"};
    private static final String[] privateFieldTypes = {"String", "int", "Behavior", "int", "int"};

    @Test
    @GradedTest(name = "Immutable properties declared 'private final'", points = 5.0)
    public void testImmutablePropertiesFinal() {
        for (int i = 0; i < privateFieldNames.length; i++) {
            String expected = "private final " + privateFieldTypes[i] + " " + privateFieldNames[i];
            assertTrue(source.contains(expected), "Did not find '" + expected + "'");
        }
    }

    private String capitalize(String s) {
        if (s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @Test
    @GradedTest(name = "Instance variables have public getters", points = 5.0)
    public void testPublicGettersExist() {
        for (int i = 0; i < privateFieldNames.length; i++) {
            String expected = "public " + privateFieldTypes[i] + " get" +
                    capitalize(privateFieldNames[i]);
            assertTrue(source.contains(expected),
                    "Did not find '" + expected + "'");
        }
        assertTrue(source.contains("public int getNumHearts"),
                "Did not find public getter for numHearts");
    }

    @Test
    @GradedTest(name = "Implementation of toString()", points = 5.0)
    public void testToString() {
        int overrideIndex = source.indexOf("@Override");
        assertTrue(overrideIndex >= 0, "@Override annotation not found");
        assertTrue(source.indexOf("@Override", overrideIndex + 1) == -1,
                "@Override appears more than once");
        int toStringIndex = source.indexOf("public String toString()");
        assertTrue(toStringIndex >= 0, "public String toString() not found");
        assertTrue(overrideIndex < toStringIndex && overrideIndex + 15 > toStringIndex,
                "@Override does not immediately precede toString()");
        Mob mob = new Mob("squid", 100, Mob.Behavior.Hostile, 1, 10);
        assertEquals("Healthy squid", mob.toString(),
                "toString() does not behave as expected");
    }

    @Test
    @GradedTest(name = "assert is used twice", points = 2.0)
    public void testAssertUsedTwice() {
        int index = source.indexOf("assert ");
        assertTrue(index != -1, "Could not find 'assert '");
        index = source.indexOf("assert ", index + 1);
        assertTrue(index != -1, "Could not find second use of 'assert '");
    }

    @Test
    @GradedTest(name = "No Kotlin libraries used", points = 5.0)
    public void testNoKotlinLibs() {
        assertFalse(
                source.contains("kotlin"),
                "The string 'kotlin' appears, which suggests you may be using Kotlin libraries.");
    }

    // https://www.baeldung.com/reading-file-in-java
    private static String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
}
