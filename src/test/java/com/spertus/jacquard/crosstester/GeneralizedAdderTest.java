package com.spertus.jacquard.crosstester;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.*;

@ExtendWith(DependencyInjector.class)
public class GeneralizedAdderTest extends AdderTest {
    private Class<? extends AbstractAdder> classUnderTest;
    private Constructor<? extends AbstractAdder> constructor;
    private Integer variant;

    public GeneralizedAdderTest(Class<? extends AbstractAdder> clazz) throws NoSuchMethodException {
        classUnderTest = clazz;
        variant = null;
        constructor = classUnderTest.getConstructor();
    }

    public GeneralizedAdderTest(Class<? extends AbstractAdder> clazz, int i) throws NoSuchMethodException {
        classUnderTest = clazz;
        variant = i;
        constructor = classUnderTest.getConstructor(int.class);
    }

    @Override
    public AbstractAdder makeAdder() {
        try {
            if (variant == null) {
                return constructor.newInstance();
            } else {
                return constructor.newInstance(variant);
            }
        } catch (InvocationTargetException | IllegalAccessException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
