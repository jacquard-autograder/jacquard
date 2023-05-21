package newgrader.syntaxgrader;

/**
 * A wrapper around a mutable integer value.
 */
/* default */ class MutableInteger {
    private int value;

    /**
     * Constructs a mutable integer with an initial value of 0.
     */
    /* default */ MutableInteger() {
    }

    /**
     * Increments the value by 1.
     */
    /* default */ void increment() {
        value++;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    /* default */ int getValue() {
        return value;
    }
}
