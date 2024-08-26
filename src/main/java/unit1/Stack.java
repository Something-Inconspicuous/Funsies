package unit1;

import java.util.NoSuchElementException;

public class Stack<E> /* implements Queue<E> */ {
    private Object[] data;
    private transient int size;

    public Stack() {
        this(10);
    }

    public Stack(int size) {
        super();
        data = new Object[size];
        this.size = 0;
    }

    private void grow(){
        Object[] grown = new Object[(int)(size * 1.5)];
        System.arraycopy(data, 0, grown, 0, size);
        data = grown;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void push(E e) {
        if(size == data.length){
            grow();
        }

        data[size] = e;
        size++;
    }

    public E pop() {
        if(isEmpty()) throw new NoSuchElementException("Stack is empty.");

        size--;
        @SuppressWarnings("unchecked")
        E ret = (E) data[size];

        // Null the pointer here so that garbage collection
        // is not potentially lied to by our array.
        data[size] = null;
        return ret;
    }

    @SuppressWarnings("unchecked")
    public E peek() {
        return (E) data[size - 1];
    }

    public int search(E e) {
        for(int i = size - 1; i >= 0; i--){
            if(data[i].equals(e)) {
                return i;
            }
        }
        return -1;
    }

    public boolean contains(E e) {
        return search(e) != -1;
    }
     
}