package student;

import com.github.javaparser.ast.CompilationUnit;
import newgrader.Autograder;
import newgrader.common.Result;
import newgrader.junit5integration.JUnitRunner;
import newgrader.syntaxgrader.ImportChecker;

import java.io.File;
import java.util.List;

public class Homework1Grader {
    public static void main(String[] args) {
        JUnitRunner runner = new JUnitRunner();
        List<Result> results = runner.runTestClass(OriginalMobTest.class);
        //  List<Result> results = runner.runTestClasses(OriginalMobTest.class, MobTest.class);
        // List<Result> results = runner.runTestPackage("student");
        ImportChecker importChecker = new ImportChecker("Kotlin import checker", 1.0, "kotlin", false);
        CompilationUnit cu = Autograder.parse(new File("src/main/java/student/Mob.java"));
        results.addAll(importChecker.grade(cu));
        importChecker = new ImportChecker("Java import checker", 1.0, "java.util.", true);
        results.addAll(importChecker.grade(cu));
        System.out.println(results);
    }
}
