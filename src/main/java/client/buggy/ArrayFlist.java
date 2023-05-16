package client.buggy;

import client.staff.Flist;

import java.util.Arrays;
import java.util.Objects;

// size() always returns 0
public class ArrayFlist<T extends Comparable<T>> implements Flist<T> {
    // If this constant is changed, also change the constructor javadoc.
    private static final int INITIAL_CAPACITY = 10;

    private int size;   // number of items in the array, not its capacity
    private T[] array;

    /**
     * Creates an empty list with an initial capacity of ten.
     */
    public ArrayFlist() {
        array = (T[]) new Object[INITIAL_CAPACITY];
    }

    /**
     * Creates an empty list with the specified contents and a capacity
     * just large enough to hold them.
     *
     * @param items the initial items
     */
    @SafeVarargs
    public ArrayFlist(T... items) {
        array = Arrays.copyOf(items, items.length);
        size = items.length;
    }

    // Methods defined in Object
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ArrayFlist<?> otherList)) {
            return false;
        }
        if (this.size() != otherList.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!Objects.equals(this.get(i), otherList.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size - 1; i++) {
            sb.append(get(i).toString()).append(", ");
        }
        sb.append(get(size - 1).toString()).append(']');
        return sb.toString();
    }

    // Methods defined in Flist
    @Override
    public void add(T item) {
        if (isFull()) {
            doubleCapacity();
        }
        array[size++] = item;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public T get(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Negative argument not permitted for get()");
        }
        if (index >= size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Index %d is out of bounds for list of size %d",
                            index, size()));
        }
        return (T) array[index];
    }

    // Private helper methods
    private boolean isFull() {
        return size == getCapacity();
    }

    private void doubleCapacity() {
        ensureCapacity(2 * getCapacity());
    }

    private int getCapacity() {
        return array.length;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > getCapacity()) {
            // Allocate a bigger array.
            T[] newArray = (T[]) new Object[minCapacity];

            // Copy all elements of the old array into the new array.
            System.arraycopy(array, 0, newArray, 0, array.length);

            // Make the instance variable point to the new array.
            array = newArray;
        }
    }
}
