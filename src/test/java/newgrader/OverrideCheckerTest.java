package newgrader;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OverrideCheckerTest {
    private static final double SCORE_PER_OVERRIDE = .5;
    private static final String OVERRIDE_TEMPLATE = """
            class MyClass extends Something {
                %s
                public double foo() { return 1.0; }

                %s
                public void bar(String s) { println(s); }
            }
            """;

    interface Interface {
        double foo();

        void bar(String s);
    }

    abstract static class Class {
        abstract double foo();

        void bar(String s) {
        }

        static void notMe() {
        }

        final String notMeEither(char c) {
            return null;
        }
    }

    private void testAllCombinations(OverrideChecker checker) {
        assertEquals(2 * SCORE_PER_OVERRIDE, checker.getTotalMaxScore());

        // override none
        List<Result> results0 = checker.process(
                TestUtilities.parseProgramFromClass(
                        String.format(OVERRIDE_TEMPLATE, "", "")));
        assertEquals(0 * SCORE_PER_OVERRIDE, TestUtilities.getTotalScore(results0));

        // override first
        List<Result> results1a = checker.process(
                TestUtilities.parseProgramFromClass(
                        String.format(OVERRIDE_TEMPLATE, "@Override", "")));
        assertEquals(SCORE_PER_OVERRIDE, TestUtilities.getTotalScore(results1a));

        // override second
        List<Result> results1b = checker.process(
                TestUtilities.parseProgramFromClass(
                        String.format(OVERRIDE_TEMPLATE, "", "@Override")));
        assertEquals(SCORE_PER_OVERRIDE, TestUtilities.getTotalScore(results1b));

        // override both
        List<Result> results2 = checker.process(
                TestUtilities.parseProgramFromClass(
                        String.format(OVERRIDE_TEMPLATE, "@Override", "@Override")));
        assertEquals(2 * SCORE_PER_OVERRIDE, TestUtilities.getTotalScore(results2));
    }

    @Test
    public void makeOverrideCheckerFromMethodListWithInterface() {
        OverrideChecker checker = OverrideChecker.makeOverrideCheckerFromMethodList(
                "Override checker",
                SCORE_PER_OVERRIDE,
                Arrays.asList(Interface.class.getMethods()));
        testAllCombinations(checker);
    }

    @Test
    public void makeOverrideCheckerFromMethodListWithClass() throws NoSuchMethodException {
        OverrideChecker checker = OverrideChecker.makeOverrideCheckerFromMethodList(
                "Override checker",
                SCORE_PER_OVERRIDE,
                List.of(
                        Class.class.getDeclaredMethod("foo"),
                        Class.class.getDeclaredMethod("bar", String.class)));
        testAllCombinations(checker);

        // static method
        assertThrows(IllegalArgumentException.class,
                () -> OverrideChecker.makeOverrideCheckerFromMethodList(
                        "Override checker",
                        SCORE_PER_OVERRIDE,
                        List.of(Class.class.getDeclaredMethod("notMeEither", char.class))));

        // final method
        assertThrows(IllegalArgumentException.class,
                () -> OverrideChecker.makeOverrideCheckerFromMethodList(
                        "Override checker",
                        SCORE_PER_OVERRIDE,
                        List.of(Class.class.getDeclaredMethod("notMe"))));
    }

    @Test
    public void makeAllAllowableMethodCheckerWithClass() {
        OverrideChecker checker = OverrideChecker.makeAllAllowableMethodChecker(
                "Override checker",
                SCORE_PER_OVERRIDE,
                Class.class);
        testAllCombinations(checker);
    }

    @Test
    public void makeAllAllowableMethodCheckerWithInterface() {
        OverrideChecker checker = OverrideChecker.makeAllAllowableMethodChecker(
                "Override checker",
                SCORE_PER_OVERRIDE,
                Interface.class);
        testAllCombinations(checker);
    }

    @Test
    public void makeAllAbstractMethodCheckerWithInterface() {
        OverrideChecker checker = OverrideChecker.makeAllAbstractMethodChecker(
                "Override checker",
                SCORE_PER_OVERRIDE,
                Interface.class);
        testAllCombinations(checker);
    }

    @Test
    public void makeAllAbstractMethodCheckerWithClass() throws NoSuchMethodException {
        // This counts foo() only.
        OverrideChecker checker = OverrideChecker.makeAllAbstractMethodChecker(
                "Override checker",
                SCORE_PER_OVERRIDE,
                Class.class);

        // Override foo().
        List<Result> results1a = checker.process(
                TestUtilities.parseProgramFromClass(
                        String.format(OVERRIDE_TEMPLATE, "@Override", "")));
        assertEquals(SCORE_PER_OVERRIDE, TestUtilities.getTotalScore(results1a));

        // Override bar().
        List<Result> results1b = checker.process(
                TestUtilities.parseProgramFromClass(
                        String.format(OVERRIDE_TEMPLATE, "", "@Override")));
        assertEquals(0, TestUtilities.getTotalScore(results1b));

        // Override both.
        List<Result> results2 = checker.process(
                TestUtilities.parseProgramFromClass(
                        String.format(OVERRIDE_TEMPLATE, "@Override", "@Override")));
        assertEquals(SCORE_PER_OVERRIDE, TestUtilities.getTotalScore(results2));
    }
}
