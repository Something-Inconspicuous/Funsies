/*
 * Unit 1.1: Stack Practice
 */

package unit1;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A simple stack implementation using an array.
 * <p>
 * This specific stack implementation is not very good; it is not thread safe, lacks
 * many operations standard of other java collections, and probably some other issues.
 * <p>
 * That being said, it is simple and meets the requirements of this assignment, and normally
 * I would grossly overengineer it. I didn't this time.
 * 
 * @param <E> The type of element to store in this stack.
 */
public class Stack<E> /* implements Queue<E> */ {
    private Object[] data;
    private transient int size;

    /**
     * Constructs a stack with an initial capacity of 10.
     */
    public Stack() {
        this(10);
    }

    /**
     * Constructs a stack with a given initial capacity.
     * 
     * @param size The initial capacity of the stack.
     */
    public Stack(int size) {
        super();
        data = new Object[size];
        this.size = 0;
    }

    /**
     * Grow the stack by 50%
     */
    private void grow(){
        Object[] grown = new Object[(int)(size * 1.5)];
        System.arraycopy(data, 0, grown, 0, size);
        data = grown;
    }

    /**
     * Gets the size of the stack, i.e. the number of elements in the stack
     * 
     * @return The size of the stack
     */
    public int size() {
        return size;
    }

    /**
     * Checks if the stack is empty.
     * 
     * @return {@code true} if the stack has no elements, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Pushes an element to the top of the stack. Said element will
     * then be the top element, provided no others are pushed.
     * 
     * @param e The element to push or add.
     */
    public void push(E e) {
        if(size == data.length){
            grow();
        }

        data[size] = e;
        size++;
    }

    /**
     * Pops the top element off the stack and returns it.
     * 
     * @throws NoSuchElementException If the stack is empty.
     * 
     * @return The element previously on top of the stack.
     */
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

    /**
     * Peeks the top element without removing it.
     * 
     * @throws NoSuchElementException If the stack is empty.
     * 
     * @return The element on top of the stack.
     */
    @SuppressWarnings("unchecked")
    public E peek() {
        if(isEmpty()) throw new NoSuchElementException("Stack is empty.");
        return (E) data[size - 1];
    }

    /**
     * Linearly searches down the stack for the given element, returning
     * its depth in the stack. The top element is at depth == 0.
     * 
     * @param e The element to search for.
     * @return The depth of the given element, or -1 if it is not present in the stack.
     */
    public int search(E e) {
        for(int i = size - 1; i >= 0; i--){
            if(data[i].equals(e)) {
                // Convert from array index to depth
                return size - i - 1;
            }
        }
        return -1;
    }

    /**
     * Checks if an element is present in the stack.
     * 
     * @param e The element to search for.
     * @return {@code true} if the element is in the stack, {@code false} otherwise.
     */
    public boolean contains(E e) {
        return search(e) != -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stack<?> stack = (Stack<?>) o;
        return Objects.deepEquals(data, stack.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }
}