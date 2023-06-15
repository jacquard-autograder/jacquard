package student;

import newgrader.common.Result;
import newgrader.junit5integration.JUnitRunner;

import java.util.List;

public class Homework1Grader {
    public static void main(String[] args) {
        JUnitRunner runner = new JUnitRunner();
        List<Result> results = runner.runTest(OriginalMobTest.class);
        System.out.println(results);
    }
}
