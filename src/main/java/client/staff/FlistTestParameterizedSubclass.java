package client.staff;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class FlistTestParameterizedSubclass<E extends Class<Flist<?>>> extends FlistTest {
    private final Constructor<? extends Flist<?>> constructor;

    public FlistTestParameterizedSubclass(Class<? extends Flist<?>> clazz) throws NoSuchMethodException {
        constructor = clazz.getConstructor(Object[].class);
    }

    @SafeVarargs
    @Override
    public final <T> Flist<T> buildFlist(T... items) {
        Object[] params = {items};
        try {
            return (Flist<T>) constructor.newInstance(params);
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
