package unit3.test;

import unit3.BinarySearchTree;

public class BSTTest {
    public static void main(String[] args) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<Integer>(0);
        
        int last = 0;
        for(int i = 0; i < 60; i++) {
            bst.insert(last = (int)(Math.random() * 50) + 1);
        }

        System.out.println(bst.contains(last));

        System.out.println(bst.contains(99));

        System.out.println(bst);
    }
}
