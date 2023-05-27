package client.staff;

import newgrader.crossgrader.DependencyInjector;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@ExtendWith(DependencyInjector.class)
public class GeneralizedFlistTest extends FlistTest {
    private static Class<? extends Flist<?>> classToTest; // injection
    private final Constructor<? extends Flist<?>> constructor;

    public GeneralizedFlistTest() throws NoSuchMethodException {
        constructor = classToTest.getConstructor(Object[].class);
    }

    public GeneralizedFlistTest(String className) throws ClassNotFoundException, NoSuchMethodException {
        Class<? extends Flist<?>> clazz = (Class<? extends Flist<?>>) Class.forName(className);
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
