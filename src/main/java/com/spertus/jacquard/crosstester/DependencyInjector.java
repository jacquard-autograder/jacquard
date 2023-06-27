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

    static void reset() {
        generalizedTestClass = null;
        classToInject = null;
        intToInject = null;
    }

    static void setGeneralizedTestClass(Class<?> generalizedTestClass) {
        DependencyInjector.generalizedTestClass = generalizedTestClass;
    }

    static void setClassToInject(Class<?> classToInject) {
        DependencyInjector.classToInject = classToInject;
    }

    static void setIntToInject(int i) {
        intToInject = i;
    }

    @Override
    public Object createTestInstance(TestInstanceFactoryContext factoryContext, ExtensionContext extensionContext) throws TestInstantiationException {
        // This local variable simplifies exception reporting.
        String argTypes = intToInject == null ? "Class<?>" : "Class<?>, int";
        try {
            if (intToInject == null) {
                Constructor<?> constructor = generalizedTestClass.getConstructor(Class.class);
                return constructor.newInstance(classToInject);
            } else {
                Constructor<?> constructor = generalizedTestClass.getConstructor(Class.class, int.class);
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
        } catch (Exception e) {
            throw new TestInstantiationException(
                    "Crossgrading failed due to an internal autograder error. ", e);
        }
    }
}
