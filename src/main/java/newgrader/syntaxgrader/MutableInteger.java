package newgrader.syntaxgrader;

/**
 * A wrapper around a mutable integer value.
 */
class MutableInteger {
    private int value = 0;

    /**
     * Constructs a mutable integer with an initial value of 0.
     */
    MutableInteger() {
    }

    /**
     * Increments the value by 1.
     */
    void increment() {
        value++;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    int getValue() {
        return value;
    }
}
