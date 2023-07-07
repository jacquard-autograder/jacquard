package com.spertus.jacquard.crosstester;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

// The class and method don't need to be abstract.
// They just can't be final.
@Tag("IndirectTest")
public abstract class AdderTest {
    public abstract AbstractAdder makeAdder();

    @Test
    public void addZero() {
        assertEquals(1, makeAdder().add(1, 0));
        assertEquals(2, makeAdder().add(0, 2));
    }
}
