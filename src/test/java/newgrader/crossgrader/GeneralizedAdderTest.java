package newgrader.crossgrader;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(DependencyInjector.class)
public class GeneralizedAdderTest extends AdderTest {
    Class<? extends AbstractAdder> classUnderTest;

    public GeneralizedAdderTest(Class<? extends AbstractAdder> clazz) {
        classUnderTest = clazz;
    }

    @Override
    public AbstractAdder makeAdder() {
        try {
            return classUnderTest.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
