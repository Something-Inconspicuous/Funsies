package unit1;

import java.io.PrintStream;
import java.util.*;

public class LinkedList<E> implements Iterable<E> {
    private static final class Node<F> {
        private F val;
        private Node<F> next;
        private Node<F> prev;

        public Node(F val, Node<F> next, Node<F> prev) {
            this.val = val;
            this.next = next;
            this.prev = prev;
        }

        public boolean hasNext() {
            return next != null;
        }

        public Node<F> getNext() {
            return next;
        }

        public boolean hasPrev() {
            return prev != null;
        }

        public Node<F> getPrev() {
            return prev;
        }

        public void prepend(Node<F> node) {
            node.next = this;
            this.prev = node;
        }

        public void append(Node<F> node) {
            this.next = node;
            node.prev = this;
        }

        public F get() {
            return val;
        }

        public void set(F val) {
            this.val = val;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return Objects.equals(val, node.val) && Objects.equals(next, node.next) && Objects.equals(prev, node.prev);
        }

        @Override
        public int hashCode() {
            return val.hashCode();
        }

        @Override
        public String toString() {
            return "Node{" +
                    "val=" + val +
                    '}';
        }
    }

    private Node<E> first;
    private Node<E> last;

    private int size;

    protected int modCount;

    public LinkedList() {}

    public void add(E e) {
        if(first == null) {
            bindHead(e);
            return;
        }

        modCount++;
        Node<E> addend = new Node<>(e, null, last);
        last.next = addend;
        last = addend;

        size++;
    }

    public void insert(int i, E e) {
        if(i == size()){
            add(e);
            return;
        }

        checkIndex(i);

        Node<E> node = getNode(i);
        Node<E> addend = new Node<>(e, node, node.prev);
        node.prev.next = addend;
        node.prev = addend;

        size++;
        modCount++;
    }

    private void bindHead(E e) {
        first = new Node<>(e, null, null);
        last = first;
        size = 1;
        modCount++;
    }

    public E removeFirst() {
        if (first == null) throw new NoSuchElementException("LinkedList is empty");

        modCount++;
        size--;

        Node<E> ret = first;
        // Simply move the `first` pointer up one
        first = first.next;
        first.prev = null;

        return ret.get();
    }

    public E removeLast() {
        if(last == null) throw new NoSuchElementException("LinkedList is empty");

        modCount++;
        size--;

        Node<E> ret = last;
        last = last.prev;
        last.next = null;

        return ret.get();
    }

    public E remove(int i) {
        checkIndex(i);

        // Simpler cases handled here
        if(i == 0)
            return removeFirst();
        if(i == size() - 1)
            return removeLast();

        // Generic case
        Node<E> toRemove = getNode(i);
        removeNode(toRemove);

        return toRemove.get();
    }

    public boolean removeFirst(E e) {
        if(first == null) return false;

        Node<E> node = first;
        do {
            if(Objects.equals(e, node.get())) {
                removeNode(node);
                return true;
            }
            node = node.next;
        } while (node.hasNext());

        return false;
    }

    private void removeNode(Node<E> toRemove) {
        toRemove.prev.next = toRemove.next;
        toRemove.next.prev = toRemove.prev;
        modCount++;
        size--;
    }

    public E get(int i) {
        return getNode(i).get();
    }
    public void set(int i, E value) {
        getNode(i).set(value);
    }

    private Node<E> getNode(int i) {
        checkIndex(i);

        // Try to get from the closer end.
        int mid = size() / 2;
        if(i > mid)
            return getNodeFromTail(i, mid);
        else
            return getNodeFromHead(i);
    }

    private void checkIndex(int i) {
        if(i < 0 || i >= size()) throw new IndexOutOfBoundsException(i);
    }

    private Node<E> getNodeFromHead(int i) {
        Node<E> node = first;
        while (i --> 0) {
            // Trust that index has been checked
            node = node.next;
        }
        return node;
    }

    private Node<E> getNodeFromTail(int i, int mid) {
        Node<E> node = last;
        while (i --> mid) {
            // Trust that index has been checked
            node = node.prev;
        }
        return node;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(E e) {
        for(E el : this) {
            if(Objects.equals(e, el))
                return true;
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            final int expectedModCount = modCount;
            Node<E> node = first;
            Node<E> prev = null;

            private void check() {
                if (expectedModCount != modCount) {
                    throw new ConcurrentModificationException();
                }
            }

            @Override
            public boolean hasNext() {
                check();
                return node.hasNext();
            }

            @Override
            public E next() {
                check();
                prev = node;
                node = node.next;
                return prev.get();
            }

            @Override
            public void remove() {
                check();
                if (prev == null) {
                    throw new IllegalStateException("Cannot remove.");
                }

                if(prev.hasPrev()) {
                    prev.prev.append(node);
                    node.prepend(prev.prev);
                } else {
                    // Simply move the `first` pointer up one
                    first = first.next;
                    first.prev = null;
                }
            }
        };
    }

    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(iterator(), size(), Spliterator.SIZED);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkedList<?> that = (LinkedList<?>) o;
        return size == that.size && modCount == that.modCount && Objects.equals(first, that.first) && Objects.equals(last, that.last);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, last, size, modCount);
    }

    @Override
    public String toString() {
        // Simple cases
        if(first == null) return "[]";
        if(first == last) return "[" + first.get() + "]";

        // General case
        StringBuilder sb = new StringBuilder("[");

        Node<E> node = first;
        while(true) {
            E obj = node.get();
            if(obj == this)
                sb.append("this");
            else
                sb.append(obj);

            if(node.hasNext()) {
                sb.append(", ");
                node = node.next;
            } else {
                break;
            }
        }
        return sb.append("]").toString();
    }

    // These are stupid
    private void print(PrintStream s) {
        s.println(this);
    }

    public void print() {
        print(System.out);
    }
}
