package student;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrimeCheckerTest {
    @Test
    public void testIs7Prime() {
        PrimeChecker checker = new PrimeChecker();
        assertTrue(checker.isPrime(7));
    }
}
