package unit1;

public class LinkedDeque<E> {
    private final LinkedList<E> data;

    public LinkedDeque() {
        data = new LinkedList<>();
    }

    public void offerFirst(E e) {
        data.insert(0, e);
    }

    public E pollFirst() {
        return data.remove(0);
    }

    public E peekFirst() {
        return data.get(data.size() - 1);
    }

    public void offerLast(E e) {
        data.add(e);
    }

    public E pollLast() {
        return data.remove(data.size() - 1);
    }

    public E peekLast() {
        return data.get(data.size() - 1);
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
