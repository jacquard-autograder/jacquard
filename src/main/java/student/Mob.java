// correct implementation

package student;

import java.util.Random;

/**
 * A Minecraft mob.
 */
public class Mob {
    private final String type;
    private final int maxHearts;
    private final Behavior behavior;
    private final int minDamage;
    private final int maxDamage;
    private int numHearts;

    public Mob(String type, int maxHearts, Behavior behavior, int minDamage, int maxDamage) {
        this.type = type;
        this.maxHearts = maxHearts;
        this.behavior = behavior;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.numHearts = maxHearts;
    }

    public String getType() {
        return type;
    }

    public int getMaxHearts() {
        return maxHearts;
    }

    public Behavior getBehavior() {
        return behavior;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public int getNumHearts() {
        return numHearts;
    }

    public Status getStatus() {
        if (numHearts == maxHearts) {
            return Status.Healthy;
        } else if (numHearts == 0) {
            return Status.Dead;
        } else {
            return Status.Injured;
        }
    }

    public boolean isAlive() {
        return getStatus() != Status.Dead;
    }

    public boolean isAggressive() {
        if (!isAlive()) {
            return false;
        }
        return switch (behavior) {
            case Passive -> false;
            case Boss, Hostile -> true;
            case Neutral -> getStatus() == Status.Injured;
        };
    }

    @Override
    public String toString() {
        return getStatus() + " " + type;
    }

    public enum Behavior {
        Passive,
        Hostile,
        Neutral,
        Boss
    }

    public enum Status {
        Healthy,
        Injured,
        Dead
    }

    /**
     * Takes up to the specified amount of damage, to a maximum of
     * {@link #numHearts}, printing a message with the amount of damage
     * taken and the new status.
     *
     * @param damage the amount of damage inflicted
     */
    public void takeDamage(int damage) {
        int actualDamage = damage > numHearts ? numHearts : damage;
        numHearts -= actualDamage;
        String text = actualDamage == 1 ? "heart" : "hearts";
        System.out.printf("The %s took %d %s of damage.", type, actualDamage, text);
        System.out.println("It is now " + getStatus() + ".");
    }

    /**
     * Attacks another mob.
     *
     * @param victim the mob to attack
     */
    public void attack(Mob victim) {
        assert (isAggressive());
        int damage = new Random().nextInt(maxDamage - minDamage) + minDamage;
        victim.takeDamage(damage);
    }

    /**
     * Simulates a battle to the death with another mob.
     *
     * @param opponent the other mob
     */
    public void battle(Mob opponent) {
        assert (this.isAggressive() && opponent.isAggressive());
        while (this.isAlive() && opponent.isAlive()) {
            if (this.isAggressive()) {
                attack(opponent);
            }
            if (opponent.isAggressive()) {
                opponent.attack(this);
            }
        }
        Mob winner = isAlive() ? this : opponent;
        System.out.printf("The %s won the battle!", winner);
    }

    public static void doBattle() {
        Mob mob1 = new Zombie();
        Mob mob2 = new Spider();
        mob1.battle(mob2);
    }

    public static void main(String[] arts) {
        MobTest.runTests();
        doBattle();
    }

    /**
     * Tests of {@link Mob}.
     */
    public class MobTest {
        /**
         * Verifies that the two values are equal.
         *
         * @param expected the expected value
         * @param actual   the actual value
         * @throws AssertionError if they are not equal
         */
        public static void assertEquals(Object expected, Object actual) {
            if (expected != actual) {
                throw new AssertionError(
                        String.format("Expected %s, got %s", expected, actual));
            }
        }

        public static void testIsAlive() {
            Mob zombie = new Zombie();
            assertEquals(true, zombie.isAlive()); // Healthy
            zombie.takeDamage(1);
            assertEquals(true, zombie.isAlive()); // Injured
            zombie.takeDamage(20);
            assertEquals(false, zombie.isAlive()); // Dead
        }

        public static void testIsAggressive() {
            assertEquals(
                    true,
                    new EnderDragon().isAggressive()
            );
            assertEquals(
                    true,
                    new Skeleton().isAggressive()
            );
            assertEquals(
                    false,
                    new Cow().isAggressive()
            );

            // A neutral mob is aggressive only when injured.
            Mob spider = new Spider();
            assertEquals(false, spider.isAggressive()); // healthy
            spider.takeDamage(1);
            assertEquals(true, spider.isAggressive()); // injured
            spider.takeDamage(19);
            assertEquals(false, spider.isAggressive()); // dead
        }

        public static void runTests() {
            testIsAlive();
            testIsAggressive();
            System.out.println("All tests pass.");
        }
    }
}

class Cow extends Mob {
    Cow() {
        super("cow", 5, Behavior.Passive, 0, 0);
    }
}

class EnderDragon extends Mob {
    EnderDragon() {
        super("Ender dragon", 200, Behavior.Boss, 1, 2);
    }
}

class Skeleton extends Mob {
    Skeleton() {
        super("skeleton", 15, Behavior.Hostile, 1, 2);
    }
}

class Spider extends Mob {
    Spider() {
        super("spider", 15, Behavior.Neutral, 3, 8);
    }
}

class Zombie extends Mob {
    Zombie() {
        super("zombie", 20, Behavior.Hostile, 1, 4);
    }
}
