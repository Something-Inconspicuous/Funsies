package unit1.test;

import unit1.Linter;
import unit1.Stack;

import java.io.File;
import java.io.FileNotFoundException;

public class LinterTest {
    public static void main(String[] args) throws FileNotFoundException {
        Linter.lint(new File("code.txt"));
    }
}
