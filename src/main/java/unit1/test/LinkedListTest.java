package unit1.test;

import unit1.LinkedList;

public class LinkedListTest {
    public static void main(String[] args) {
        LinkedList<Object> ll = new LinkedList<>();

        ll.add("hello");
        ll.add("world");

        ll.add(ll);

        ll.add(2);

        ll.insert(1, 'c');

        System.out.println(ll);

        ll.add("world");

        ll.removeFirst("world");

        System.out.println(ll);
    }
}
