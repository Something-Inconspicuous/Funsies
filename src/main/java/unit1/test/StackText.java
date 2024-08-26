package unit1.test;

import java.util.NoSuchElementException;

import unit1.Stack;

/**
 * StackText
 */
public class StackText {

    public static void main(String[] args) {
        Stack<Integer> stack = new Stack<>();

        System.out.println(stack.isEmpty());

        for(int i = 0; i < 20; i++){
            stack.push(Integer.valueOf((int)(Math.random() * 101)));
        }

        System.out.println(stack.isEmpty());
        System.out.println(stack.peek());
        System.out.println(stack.pop());
        System.out.println(stack.isEmpty());

        for(int i = 0; i < 18; i++) {
            System.out.println(stack.pop());
        }

        System.out.println(stack.isEmpty());
        System.out.println(stack.pop());

        try {
            System.out.println(stack.pop());
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }

        System.out.println(stack.isEmpty());
    }
}