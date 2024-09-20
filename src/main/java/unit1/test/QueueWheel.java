package unit1.test;

import unit1.LinkedQueue;

import java.util.Scanner;

public class QueueWheel {
    public static void main(String[] args) throws InterruptedException {
        LinkedQueue<String> wheel = new LinkedQueue<>();

        System.out.println("Fill up the wheel, then \"spin!\"");
        Scanner input = new Scanner(System.in);
        String token = input.nextLine();
        while (!token.equalsIgnoreCase("spin!")) {
            wheel.offer(token);
            token = input.nextLine();
        }

        if(wheel.isEmpty()) {
            System.out.println("Boring");
            return;
        }

        // Spinning
//        clear();

        // maximum 6 spins
        int clicks = (int)(Math.random() * wheel.size() * 6 + wheel.size());

        while(clicks --> 0) {
            clear();
            System.out.println(wheel.peek());
            wheel.offer(wheel.poll());

            // small gap to give time to read
            Thread.sleep(200);
        }

        clear();

        System.out.println("========================\n" + wheel.peek() + "\n========================");
    }

    private static void clear() {
        System.out.flush();
        System.out.print("\033[H\033[2J");
    }
}
