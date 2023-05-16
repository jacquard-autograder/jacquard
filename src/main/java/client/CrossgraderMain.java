package client;

import client.staff.GeneralizedFlistTest;
import newgrader.crossgrader.CrossGrader;

public class CrossgraderMain {

    public static void main(String[] args) throws NoSuchMethodException {
        CrossGrader grader = new CrossGrader(
                GeneralizedFlistTest.class,
                CrossgraderMain.class.getClassLoader().getResourceAsStream("scores.csv"));
        System.out.println(grader.gradeAll());
    }
}
