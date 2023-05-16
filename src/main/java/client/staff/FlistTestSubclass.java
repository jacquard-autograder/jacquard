package client.staff;

public class FlistTestSubclass extends FlistTest {
    private final FlistBuilder builder;

    protected FlistTestSubclass(FlistBuilder builder) {
        this.builder = builder;
    }

    @Override
    public <T> Flist<T> buildFlist(T... items) {
        return builder.build(items);
    }
}
