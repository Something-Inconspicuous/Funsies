package unit2;

import java.util.Arrays;

public class AlgorithmTimings {

    public static void main(String[] args) {
        for(int i = 10; i <= 50; i += 5) {
            System.out.print(i + " ");
            int finalI = i;
            System.out.print(1000 * Time.seconds(() -> recFib(finalI)) + " ");
            System.out.println(1000 * Time.seconds(() -> fastFib(finalI)));
        }
    }

    private static long recFib(int nth) {
        if(nth == 0) return 0;
        if(nth <= 2) return 1;

        return recFib(nth - 1) + recFib(nth - 2);
    }

    static final double sqrt5 = Math.sqrt(5.0);
    static final double phi = (1.0 + sqrt5) * 0.5;
    private static long fastFib(int nth) {
        return (long)((Math.pow(phi, (double) nth) - Math.pow(1.0 - phi, (double) nth)) / sqrt5);
    }
}
