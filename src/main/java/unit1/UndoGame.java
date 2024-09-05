package unit1;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class UndoGame {
    public static final String HELP_STRING = """
            ===========================
            | NIM                     |
            ===========================
            | 'q' to exit.            |
            | 'u' to undo.            |
            | 'h' or '?' to show help |
            ===========================""";
    private final int maxTake;

    private int n;

    private final Stack<Move> moves;

    private static final class Move {
        int cpuTake;
        int playerTake;

        public Move(int cpuTake, int playerTake) {
            this.cpuTake = cpuTake;
            this.playerTake = playerTake;
        }

        // Boilerplate stuff
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Move move = (Move) o;
            return cpuTake == move.cpuTake && playerTake == move.playerTake;
        }

        @Override
        public int hashCode() {
            return Objects.hash(cpuTake, playerTake);
        }

        @Override
        public String toString() {
            return "Move{" +
                    "cpuTake=" + cpuTake +
                    ", playerTake=" + playerTake +
                    '}';
        }
    }

    public UndoGame(int startNum, int maxTake) {
        this.n = startNum;
        this.maxTake = maxTake;
        moves = new Stack<>();
    }

    public UndoGame(int startNum) {
        this(startNum, 3);
    }

    public int getRemaining() {
        return n;
    }

    public boolean take(int n) {
        if(n > maxTake || n < 1) return false;
        if(this.n - n < 0) return false;
        this.n -= n;
        return true;
    }

    public boolean isGameOver() {
        return n <= 0;
    }

    public int cpuTurn() {
        // Try to take remaining to win
        final int _n = n;
        if(take(n)) return _n;

        // Play randomly
        final int take = Math.min((int) (Math.random() * maxTake + 1), n);
        take(take);
        return take;
    }

    /**
     * Plays the nim game in a new thread using the given IO.
     *
     * @param out Where to output direction and game information.
     * @param in Where to read game input.
     */
    public void play(OutputStream out, InputStream in) {
        final PrintStream _out;
        if(out instanceof PrintStream)
            _out = (PrintStream) out;
        else
            _out = new PrintStream(out);

        final Scanner _in = new Scanner(in);

        new Thread(() -> {
            boolean doPlay = true;
            _out.println(HELP_STRING);
            while(doPlay) {
                boolean couldTake = false;
                do {
                    final Move move = new Move(0, 0);

                    _out.format("%s\nThere are %d items left. Take 1-%d: ", this, n, maxTake);

                    String input;
                    do {
                        input = _in.nextLine().toLowerCase();
                    } while (input.isEmpty());

                    final char ch = input.charAt(0);

                    if (ch == 'h' || ch == '?') {
                        _out.println(HELP_STRING);
                        continue;
                    }

                    if (ch == 'u' && !moves.isEmpty()) {
                        Move last = moves.pop();
                        n += last.cpuTake;
                        n += last.playerTake;
                        continue;
                    }

                    if (ch == 'q') {
                        doPlay = false;
                        break;
                    }

                    final int take = ch - '0';

                    couldTake = take(take);

                    move.playerTake = take;

                    // Only computer play if we could
                    if (couldTake) {
                        move.cpuTake = cpuTurn();
                        _out.format("The computer takes %d.\n", move.cpuTake);

                        moves.push(move);
                    }
                } while (!couldTake && !isGameOver());
            }
        }, "NimGame").start();
    }

    @Override
    public String toString() {
        char[] str = new char[n];
        Arrays.fill(str, 'O');

        return "%s\n".formatted(new String(str));
    }
}
