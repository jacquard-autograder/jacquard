package newgrader.crossgrader;

import org.junit.jupiter.api.extension.*;

import java.lang.reflect.*;

public class DependencyInjector implements TestInstanceFactory {
    private static Class<?> generalizedTestClass;
    private static Class<?> classToInject;
    private static Integer intToInject;

    public static void reset() {
        generalizedTestClass = null;
        classToInject = null;
        intToInject = null;
    }

    public static void setGeneralizedTestClass(Class<?> generalizedTestClass) {
        DependencyInjector.generalizedTestClass = generalizedTestClass;
    }

    public static void setClassToInject(Class<?> classToInject) {
        DependencyInjector.classToInject = classToInject;
    }

    public static void setIntToInject(int i) {
        intToInject = i;
    }

    @Override
    public Object createTestInstance(TestInstanceFactoryContext factoryContext, ExtensionContext extensionContext) throws TestInstantiationException {
        try {
            if (intToInject == null) {
                Constructor<?> constructor = generalizedTestClass.getConstructor(Class.class);
                return constructor.newInstance(classToInject);
            } else {
                Constructor<?> constructor = generalizedTestClass.getConstructor(Class.class, int.class);
                return constructor.newInstance(classToInject, intToInject);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            System.out.println(this);
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
