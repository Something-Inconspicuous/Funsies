package unit3.test;

import java.util.Iterator;
import java.util.TreeSet;

public class TreeSetFun {
    public static void main(String[] args) {
        TreeSet<Integer> set = new TreeSet<>();

        for(int i = 0; i < 100; i++) {
            set.add((int)(Math.random() * 1000));
        }

        Iterator<Integer> it = set.iterator();

        while(it.hasNext()) {
            System.out.printf("%s ", it.next());
        }

        System.out.println();

        System.out.println(set.contains(500));

        System.out.println(set.first());

        System.out.println(set.floor(500));

        System.out.println(set.last());
        
        set.remove(300);

        System.out.println(set.size());
    }
}
