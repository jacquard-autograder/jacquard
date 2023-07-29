package com.spertus.jacquard.crosstester;

import org.junit.jupiter.api.extension.*;

import java.lang.reflect.*;

/**
 * An {@link Extension} enabling dependencies to be injected into test classes
 * for crossgrading.
 */
public class DependencyInjector implements TestInstanceFactory {
    private static Class<?> generalizedTestClass;
    private static Class<?> classToInject;
    private static Integer intToInject;

    /* default */ static void reset() {
        generalizedTestClass = null;
        classToInject = null;
        intToInject = null;
    }

    /* default */ static void setGeneralizedTestClass(final Class<?> generalizedTestClass) {
        DependencyInjector.generalizedTestClass = generalizedTestClass;
    }

    /* default */ static void setClassToInject(final Class<?> classToInject) {
        DependencyInjector.classToInject = classToInject;
    }

    /* default */ static void setIntToInject(final int i) {
        intToInject = i;
    }

    @Override
    public Object createTestInstance(
            final TestInstanceFactoryContext factoryContext,
            final ExtensionContext extensionContext) {
        // This local variable simplifies exception reporting.
        final String argTypes = intToInject == null ? "Class<?>" : "Class<?>, int";
        try {
            if (intToInject == null) {
                final Constructor<?> constructor = generalizedTestClass.getConstructor(Class.class);
                return constructor.newInstance(classToInject);
            } else {
                final Constructor<?> constructor = generalizedTestClass.getConstructor(Class.class, int.class);
                return constructor.newInstance(classToInject, intToInject);
            }
        } catch (NoSuchMethodException e) {
            throw new TestInstantiationException(
                    String.format("Crossgrading failed because constructor %s(%s) could not be found.",
                            generalizedTestClass.getName(),
                            argTypes),
                    e);
        } catch (InstantiationException e) {
            throw new TestInstantiationException(
                    String.format("Crossgrading failed because %s is an abstract class.",
                            generalizedTestClass.getName()),
                    e);
        } catch (IllegalAccessException e) {
            throw new TestInstantiationException(
                    String.format(
                            "Crossgrading failed because the constructor %s(%s) has the wrong visibility level.",
                            generalizedTestClass.getName(),
                            argTypes),
                    e);
        } catch (InvocationTargetException e) {
            throw new TestInstantiationException(
                    String.format(
                            "Crossgrading failed because the constructor %s(%s) threw an exception.",
                            generalizedTestClass.getName(),
                            argTypes),
                    e);
        }
    }
}
