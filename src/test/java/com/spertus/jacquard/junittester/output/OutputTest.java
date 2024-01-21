package com.spertus.jacquard.junittester.output;

import com.spertus.jacquard.junittester.GradedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IndirectTest")
public class OutputTest {
    @Test
    @GradedTest(name = "test1", description = "description1", includeOutput = false)
    public void testWithoutOutput() {
        System.out.println("output1");
        // pass
    }

    @Test
    @GradedTest(name = "test2", description = "description2", includeOutput = true)
    public void testWithOutput() {
        System.out.println("output2");
    }
}
