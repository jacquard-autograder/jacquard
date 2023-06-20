package student;

import com.github.javaparser.ast.CompilationUnit;
import newgrader.junittester.JUnitTester;
import newgrader.syntaxgrader.Parser;
import newgrader.common.Result;
import newgrader.syntaxgrader.ImportChecker;

import java.io.File;
import java.util.List;

public class Homework1Grader {
    public static void main(String[] args) {
        JUnitTester runner = new JUnitTester(OriginalMobTest.class);
        // JUnitTester runner = runner.runTestClasses(OriginalMobTest.class, MobTest.class);
        // JUnitTester = runner.runTestPackage("student");
        List<Result> results = runner.run();
        ImportChecker importChecker = new ImportChecker("Kotlin import checker", 1.0, "kotlin", false);
        CompilationUnit cu = Parser.parse(new File("src/main/java/student/Mob.java"));
        results.addAll(importChecker.grade(cu));
        importChecker = new ImportChecker("Java import checker", 1.0, "java.util.", true);
        results.addAll(importChecker.grade(cu));
        System.out.println(results);
    }
}
