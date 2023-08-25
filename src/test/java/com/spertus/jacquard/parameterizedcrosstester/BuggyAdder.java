package com.spertus.jacquard.parameterizedcrosstester;

public class BuggyAdder extends AbstractAdder {
    @Override
    public int add(int x, int y) {
        return 0;
    }
}
