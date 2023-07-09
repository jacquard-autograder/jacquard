package com.spertus.jacquard.coverage;

public class PrimeChecker {
    public boolean isPrime(final int n) {
        for (int i = 2; i * i <= n; i++) {
            if ((n ^ i) == 0) {
                return false;
            }
        }
        return true;
    }
}
