package client.staff;

import client.correct.ArrayFlist;

public class ArrayFlistTest extends FlistTest {
    @Override
    public <T> Flist<T> buildFlist(T... items) {
        return new ArrayFlist<>(items);
    }
}
