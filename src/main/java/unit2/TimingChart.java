package unit2;

import java.util.Random;

public class TimingChart {

    public static void main(String[] args) {
        fillWithMathRandom();
    }

    private static void fillWithMathRandom() {
        int[][] arrays = getArrays();

        for (int[] array : arrays) {
            long nanos = timeNanos(() -> {
                for (int i = 0; i < array.length; i++) {
                    array[i] = (int)(Math.random() * 100) + 1;
                }
            });

            System.out.println(nanos);
        }
    }

    private static void fillWithUtilRandom() {
        int[][] arrays = getArrays();

        Random rng = new Random();

        for (int[] array : arrays) {
            long nanos = timeNanos(() -> {
                for (int i = 0; i < array.length; i++) {
                    array[i] = rng.nextInt();
                }
            });

            System.out.println(nanos);
        }
    }

    private static int[][] getArrays() {
        int[][] arrays = new int[10][];

        for(int i = 0; i < arrays.length; i++) {
            arrays[i] = new int[10_000 * (i + 1)];
        }
        return arrays;
    }

    private static long timeMillis(Runnable action) {
        long start, end;
        start = System.currentTimeMillis();
        action.run();
        end = System.currentTimeMillis();
        return end - start;
    }

    private static long timeNanos(Runnable action) {
        long start, end;
        start = System.nanoTime();
        action.run();
        end = System.nanoTime();
        return end - start;
    }
}
