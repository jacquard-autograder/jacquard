package com.spertus.jacquard.crosstester;

public class ParameterizedBuggyAdder extends AbstractAdder {
    private int variant;

    public ParameterizedBuggyAdder(int variant) {
        this.variant = variant;
    }

    @Override
    public int add(int x, int y) {
        return switch(variant) {
            case 0 -> 0; // always return 0
            case 1 -> x; // always return 1st argument
            case 2 -> y; // always return 2nd argument
            default -> x + y;   // correct value
        };
    }
}
