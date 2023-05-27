package newgrader.crossgrader;

import org.junit.jupiter.api.extension.*;

import java.lang.reflect.*;

public class DependencyInjector implements TestInstanceFactory {
    private static Class<?> generalizedTestClass;
    private static Class<?> classToInject;

    public static void setClassToInject(Class<?> classToInject) {
        DependencyInjector.classToInject = classToInject;
    }

    public static void setGeneralizedTestClass(Class<?> generalizedTestClass) {
        DependencyInjector.generalizedTestClass = generalizedTestClass;
    }

    @Override
    public Object createTestInstance(TestInstanceFactoryContext factoryContext, ExtensionContext extensionContext) throws TestInstantiationException {
        try {
            Constructor<?> constructor = generalizedTestClass.getConstructor(Class.class);
            return constructor.newInstance(classToInject);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
