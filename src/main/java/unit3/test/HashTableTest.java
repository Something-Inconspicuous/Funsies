package unit3.test;

import java.util.Random;

import unit3.HashTable;

public class HashTableTest {
    public static void main(String[] args) {

        Random rng = new Random();
        HashTable<String, String> table = new HashTable<>();

        for (int i = 0; i < 160; i++) {
            table.put(randomString(rng), randomString(rng));
        }

        System.out.println(table);

        table.forEach(System.out::println);
    }

    private static String randomString(Random rng) {
        int length = rng.nextInt(2, 1 << 5);
        char[] chars = new char[length];

        for (int i = 0; i < length - 1; i++) {
            chars[i] = (char) rng.nextInt('A', 'z' + 1);
        }
        chars[length - 1] = '\0';

        return new String(chars);
    }
}
