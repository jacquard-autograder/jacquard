package client.staff;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class FlistTest {
    private Flist<Integer> emptyListInteger;
    private Flist<Integer> list1;
    private Flist<Integer> list123;
    private Flist<Integer> list321;

    public abstract <T> Flist<T> buildFlist(T... items);

    @BeforeEach
    public void setup() {
        emptyListInteger = buildFlist();
        list1 = buildFlist(1);
        list123 = buildFlist(1, 2, 3);
        list321 = buildFlist(3, 2, 1);
    }

    @Test
    public void sizeEmptyListHasSizeZero() {
        assertEquals(0, emptyListInteger.size());
    }

    @Test
    public void sizeNonEmptyListSizeReturnsSize() {
        assertEquals(1, list1.size());
        assertEquals(3, list123.size());
        assertEquals(3, list321.size());
    }

    @Test
    public void getRejectsNegativeIndex() {
        assertThrows(IllegalArgumentException.class, () -> emptyListInteger.get(-1));
        assertThrows(IllegalArgumentException.class, () -> list1.get(-5));
    }

    @Test
    public void getRejectsOutOfBoundsIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> emptyListInteger.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list1.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list123.get(3));
    }

    @Test
    public void getReturnsRightValue() {
        assertEquals(1, list123.get(0));
        assertEquals(2, list123.get(1));
        assertEquals(3, list123.get(2));
    }
}
