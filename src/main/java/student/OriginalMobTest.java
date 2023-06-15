package student;

import newgrader.common.GradedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OriginalMobTest {
    @Test
    @GradedTest(name = "Original testIsAlive()", points = 5.0)
    public  void testIsAlive() {
        if (1 == 1) {throw new IllegalArgumentException();}
        Mob zombie = new Zombie();
        assertTrue(zombie.isAlive()); // Healthy
        zombie.takeDamage(1);
        assertTrue(zombie.isAlive()); // Injured
        zombie.takeDamage(20);
        assertFalse(zombie.isAlive()); // Dead
    }

    @Test
    @GradedTest(name = "Original testIsAggressive()", points = 5.0)
    public void testIsAggressive() {
        assertTrue(new EnderDragon().isAggressive());
        assertTrue(new Skeleton().isAggressive());
        assertFalse(new Cow().isAggressive());

        // A neutral mob is aggressive only when injured.
        Mob spider = new Spider();
        assertFalse(spider.isAggressive()); // healthy
        spider.takeDamage(1);
        assertTrue(spider.isAggressive()); // injured
        spider.takeDamage(19);
        assertFalse(spider.isAggressive()); // dead
    }
}
