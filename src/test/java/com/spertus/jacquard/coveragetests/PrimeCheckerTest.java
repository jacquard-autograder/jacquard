package com.spertus.jacquard.coveragetests;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("IndirectTest")
public class PrimeCheckerTest {
    @Test
    public void testIs7Prime() {
        PrimeChecker checker = new PrimeChecker();
        assertTrue(checker.isPrime(7));
    }

    @Test
    public void badTestIs7Prime() {
        PrimeChecker checker = new PrimeChecker();
        assertFalse(checker.isPrime(7));
    }
}
