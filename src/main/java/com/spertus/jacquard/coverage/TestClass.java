package com.spertus.jacquard.coverage;

public class TestClass implements Runnable {

    public void run() {
        new ClassUnderTest().isPrime(7);
    }
}
