package com.spertus.jacquard.crosstester;

public class BuggyAdder extends AbstractAdder {
    @Override
    public int add(int x, int y) {
        return 0;
    }
}
