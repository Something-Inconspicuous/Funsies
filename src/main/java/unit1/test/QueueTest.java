package unit1.test;

import unit1.LinkedQueue;

public class QueueTest {
    public static void main(String[] args) {
        LinkedQueue<String> q = new LinkedQueue<>();

        System.out.println(q.isEmpty());

        System.out.println(q.size());

        q.offer("Alice");
        q.offer("Bob");
        q.offer("Charlie");
        q.offer("Devin");
        q.offer("Emma");
        System.out.println(q);

        System.out.println(q.isEmpty());

        System.out.println(q.peek());
        System.out.println(q.poll());

        System.out.println(q.isEmpty());

        q.poll();
        q.poll();
        q.poll();

        System.out.println(q.isEmpty());

        System.out.println(q.poll());

        System.out.println(q.isEmpty());

        System.out.println(q.size());
    }
}
