package com.spertus.jacquard.coverage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrimeCheckerTest implements Runnable {
    @Test
    public void testIs7Prime() {
        PrimeChecker checker = new PrimeChecker();
        assertTrue(checker.isPrime(7));
    }

    public void run() {
        testIs7Prime();
    }
}
