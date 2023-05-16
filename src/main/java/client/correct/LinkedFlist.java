package client.correct;

import client.staff.Flist;

public class LinkedFlist<T> implements Flist<T> {
    private Node<T> head;

    /**
     * Creates an empty list.
     */
    public LinkedFlist() {
        head = null;
    }

    public LinkedFlist(Node<T> node) {
        head = node;
    }

    /**
     * Creates an empty list with the specified contents.
     *
     * @param items the initial items
     */
    @SafeVarargs
    public LinkedFlist(T... items) {
        for (T item : items) {
            add(item);
        }
    }

    // Methods defined in Object
    @Override
    public boolean equals(Object other) {
        if (other instanceof Flist<?> otherFlist) {
            if (this.size() != otherFlist.size()) {
                return false;
            }
            for (int i = 0; i < size(); i++) {
                if (!this.get(i).equals(otherFlist.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        if (head == null) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (Node<T> node = head; node != null; node = node.next) {
            sb.append(node.value.toString()).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(']');
        return sb.toString();
    }

    // Methods defined in Flist

    @Override
    public void add(T value) {
        Node<T> newNode = new Node<>(value);
        if (head == null) {
            head = newNode;
        } else {
            head.add(newNode);
        }
    }

    @Override
    public int size() {
        if (head == null) {
            return 0;
        } else {
            return head.size();
        }
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public T get(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Negative argument not permitted for get()");
        }
        if (head == null) {
            throw new IndexOutOfBoundsException("get() called on an empty FlinkedList");
        }
        return head.get(index);
    }

    static class Node<T> {
        private T value;
        private Node<T> next;

        private Node(T value) {
            this.value = value;
        }

        private void add(Node<T> node) {
            if (this.next == null) {
                this.next = node;
            } else {
                this.next.add(node);
            }
        }

        private int size() {
            return 1 + (next == null ? 0 : next.size());
        }

        private T get(int i) {
            if (i == 0) {
                return value;
            }
            if (next == null) {
                throw new IndexOutOfBoundsException();
            }
            return next.get(i - 1);
        }
    }
}
