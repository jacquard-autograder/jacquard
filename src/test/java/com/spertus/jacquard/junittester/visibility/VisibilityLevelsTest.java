package com.spertus.jacquard.junittester.visibility;

import com.spertus.jacquard.common.Visibility;
import com.spertus.jacquard.junittester.GradedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VisibilityLevelsTest {
    @Test
    @GradedTest(points = 2.0)
    public void visibleTest1() {
        assertEquals(2, 1 + 1);
    }

    @Test
    @GradedTest(points = 2.0)
    public void visibleTest2() {
        assertEquals(2, 1 + 1);
    }

    @Test
    @GradedTest(points = 2.0, visibility = Visibility.HIDDEN)
    public void hiddenTest() {
        assertEquals(2, 1 + 1);
    }

    @Test
    @GradedTest(points = 2.0, visibility = Visibility.AFTER_DUE_DATE)
    public void afterDueDateTest() {
        assertEquals(2, 1 + 1);
    }

    @Test
    @GradedTest(points = 2.0, visibility = Visibility.AFTER_PUBLISHED)
    public void afterPublishedTest() {
        assertEquals(2, 1 + 1);
    }
}
