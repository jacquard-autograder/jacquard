package client.staff;

import client.correct.LinkedFlist;

public class LinkedFlistTest extends FlistTest {
    @Override
    public <T> Flist<T> buildFlist(T... items) {
        return new LinkedFlist<>(items);
    }
}
