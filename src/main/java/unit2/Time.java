package unit2;

public final class Time {
    private Time() {
        throw new AssertionError("Do not instantiate unit2.Time");
    }

    public static double seconds(Runnable action) {
        long nanos = nanos(action);

        return nanos * 1e-9;
    }

    public static long millis(Runnable action) {
        long start, end;
        start = System.currentTimeMillis();
        action.run();
        end = System.currentTimeMillis();
        return end - start;
    }

    public static long nanos(Runnable action) {
        long start, end;
        start = System.nanoTime();
        action.run();
        end = System.nanoTime();
        return end - start;
    }
}
