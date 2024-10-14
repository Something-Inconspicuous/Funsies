package unit2;

import java.util.ArrayList;
import java.util.Comparator;

public class ArrayListTimings {

    public static void main(String[] args) {
        @SuppressWarnings("unchecked")
        ArrayList<Integer>[] lists = new ArrayList[10];

        for(int i = 0; i < lists.length; i++) {
            lists[i] = new ArrayList<>();

            for(int j = 0; j < (i + 1) * 100000; j++) {
                lists[i].add((int)(Math.random() * 100));
            }
        }

        for(ArrayList<Integer> list : lists) {
            System.out.print(list.size() + " ");

            double seconds = Time.seconds(() -> {
                Integer x = list.get((int)(Math.random() * list.size()));
            });

            System.out.print(seconds * 1000.0 + " ");

            seconds = Time.seconds(() -> {
                list.addFirst(35);
            });

            System.out.print(seconds * 1000.0  + " ");

            seconds = Time.seconds(() -> {
                list.remove((int)(Math.random() * list.size()));
            });

            System.out.print(seconds * 1000.0  + " ");

            seconds = Time.seconds(() -> {
                boolean x = list.contains((int)(Math.random() * 100));
            });

            System.out.print(seconds * 1000.0  + " ");

            seconds = Time.seconds(() -> {
                list.sort(Comparator.naturalOrder());
            });

            System.out.println(seconds * 1000.0 );
        }
    }
}
