package com.spertus.jacquard.junittester.group;

import com.spertus.jacquard.common.Visibility;
import com.spertus.jacquard.junittester.GradedTest;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.fail;

// These 2 tests should get merged together.
@Tag("IndirectTest")
public class GroupTest2 {
    @Test
    @GradedTest(name = "visible test", visibility = Visibility.VISIBLE)
    public void visibleTest() {
        fail();
    }

    @Test
    @GradedTest(name = "after published test", visibility = Visibility.AFTER_PUBLISHED)
    public void afterPublishedTest1() {
        // pass
    }

    @Test
    @GradedTest(name = "after published test", visibility = Visibility.AFTER_PUBLISHED)
    public void afterPublishedTest2() {
        // pass
    }
}
