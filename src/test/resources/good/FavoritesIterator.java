package student;

import java.util.*;

public class FavoritesIterator<T> implements Iterator<T> {
    /**
     * Constructs an iterator over favorite items.
     *
     * @param items the items to iterate over
     */
    public FavoritesIterator(List<T> items) {
    }

    // Deliberate PMD violation (MissingOverride) on next line
    // @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public T next() {
        return null;
    }

    // Deliberate checkstyle violation on next line
    private void helper_method() {}
}
