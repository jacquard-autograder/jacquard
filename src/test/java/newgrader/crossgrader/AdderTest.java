package newgrader.crossgrader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AdderTest {
    public abstract AbstractAdder makeAdder();

    @Test
    public void addZero() {
        assertEquals(1, makeAdder().add(1, 0));
        assertEquals(2, makeAdder().add(0, 2));
    }
}
