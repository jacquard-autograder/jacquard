package com.spertus.jacquard.coverage;

/**
 * The test target we want to see code coverage for.
 */
public class ClassUnderTest  {
    public boolean isPrime(final int n) {
        for (int i = 2; i * i <= n; i++) {
            if ((n ^ i) == 0) {
                return false;
            }
        }
        return true;
    }

}
