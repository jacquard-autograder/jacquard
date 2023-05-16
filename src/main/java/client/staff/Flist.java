package client.staff;

public interface Flist<T> {
    /**
     * Adds a value to the end of this list.
     *
     * @param value the value to add
     */
    void add(T value);

    /**
     * Gets the number of items in this list.
     *
     * @return the number of items in this list
     */
    int size();

    /**
     * Checks whether this list is empty.
     *
     * @return true if it is empty, false otherwise
     */
    boolean isEmpty();

    /**
     * Gets the item at the specified index.
     *
     * @param index the 0-based index
     * @return the item at the specified index
     * @throws IndexOutOfBoundsException if index is out of range
     */
    T get(int index);
}
