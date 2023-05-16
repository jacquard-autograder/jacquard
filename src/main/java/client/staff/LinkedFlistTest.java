package client.staff;

import client.correct.LinkedFlist;

public class LinkedFlistTest extends FlistTest {
    @Override
    public <T extends Comparable<T>> Flist<T> buildFlist(T... items) {
        return new LinkedFlist<>(items);
    }
}
