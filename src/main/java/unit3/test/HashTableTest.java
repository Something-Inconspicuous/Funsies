package unit3.test;

import unit3.HashTable;

public class HashTableTest {
    public static void main(String[] args) {
        HashTable<String, String> table = new HashTable<>();

        table.put("Hello", "World");
        table.put("You", "Suck");
        table.put("password", "Correct or something");

        System.out.println(table);

        System.out.println(table.get("password"));
    }
}
