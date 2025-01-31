package unit6;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class QueueTesting {
    public static void main(String[] args) {
        Comparator<Player> comp = 
        Comparator.comparingDouble(Player::getWinRate)
            .reversed() // sort max wr
            .thenComparing(Player::getLastName)
            .thenComparing(Player::getFirstName);

        // testing
        Queue<Player> queue = new TreePriorityQueue<>(comp);
        for(int i = 0; i < 1000; i++) {
            queue.offer(Player.random());
        }

        System.out.println(queue);

        System.out.println("Queue size: " + queue.size());

        Player p = new Player("John Doe", 1.1);
        System.out.println("Adding " + p);
        queue.add(p);
        System.out.println("Contains " + p + ": " + queue.contains(p));
        Player r = Player.random();
        System.out.println("Contains " + r + ": " + queue.contains(r));
        System.out.println("Adding " + r);
        queue.add(r);
        System.out.println("Contains " + r + ": " + queue.contains(r));
        System.out.println("Removing " + r);
        queue.remove(r);
        

        System.out.println("Highest wr: " + queue.poll());
        System.out.println("Next-Highest wr: " + queue.peek());

        
        Player[] a = new Player[queue.size()]; 
        queue.toArray(a);

        System.out.println("5th Highest wr: " + a[4]);

        queue.clear();
        if(queue.isEmpty()) {
            System.out.println("Queue cleared");
        }

        System.out.println(queue);
    }
}
