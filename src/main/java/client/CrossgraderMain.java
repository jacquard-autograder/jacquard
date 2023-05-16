package client;

import client.staff.GeneralizedFlistTest;
import newgrader.crossgrader.CrossGrader;

import java.io.FileNotFoundException;

public class CrossgraderMain {

    public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
        CrossGrader grader = new CrossGrader(
                GeneralizedFlistTest.class,
                CrossgraderMain.class.getClassLoader().getResourceAsStream("scores.csv"));
        grader.grade();
    }
}
