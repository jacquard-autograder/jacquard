package client.staff;

import java.lang.reflect.InvocationTargetException;

public class FlistBuilder {
    private final Class<? extends Flist<?>> concreteClass;

    public FlistBuilder(Class<? extends Flist<?>> concreteClass) {
        this.concreteClass = concreteClass;
    }

    public <T> Flist<T> build(T... items)  {
        Object[] params = { items };
        try {
            return (Flist<T>) concreteClass.getConstructor(Object[].class).newInstance(params);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
