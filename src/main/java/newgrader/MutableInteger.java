package newgrader;

/**
 * A wrapper around a mutable integer value.
 */
class MutableInteger {
    private int value = 0;

    /**
     * Constructs a mutable integer with an initial value of 0.
     */
    public MutableInteger() {}

    /**
     * Increments the value by 1.
     */
    public void increment() {
        value++;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public int getValue() {
        return value;
    }
}
