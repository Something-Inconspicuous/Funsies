package unit1.test;

import unit1.LinkedDeque;

import java.util.Scanner;

public class DequePalindrome {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Give me something: ");

        String str = input.nextLine();

        if(isPalindrome(str)) {
            System.out.println("You gave me a palindrome");
        } else {
            System.out.println("That's not a palindrome");
        }

        input.close();
    }


    public static boolean isPalindrome(CharSequence input) {
        LinkedDeque<Character> deque = new LinkedDeque<>();

        for(int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            ch = Character.toLowerCase(ch);
            if((ch >= 'a' && ch <= 'z'))
                deque.offerLast(ch);
        }

        while(deque.size() > 1) {
            char ch1 = deque.pollFirst();
            char ch2 = deque.pollLast();

            if(ch1 != ch2)
                return false;
        }
        return true;
    }
}
