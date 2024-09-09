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
    private static final int PLAYER_WON = 1;
    private static final int CPU_WON = 2;
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
        if (out == null) {
            throw new NullPointerException("Output cannot be null.");
        }
        if (in == null) {
            throw new NullPointerException("Input cannot be null.");
        }

        final PrintStream _out = printify(out);

        final Scanner _in = new Scanner(in);

        new Thread(() -> playInThread(_out, _in), "NimGame").start();
    }

    private void playInThread(PrintStream out, Scanner in) {
        out.println(HELP_STRING);
        boolean couldTake = false;
        int winner = 0;
        do {
            final Move move = new Move(0, 0);

            out.format("There are %d items left. Take 1-%d: ", n, maxTake);

            final char ch = getValidInputChar(in);

            if (ch == 'h' || ch == '?') {
                out.println(HELP_STRING);
                continue;
            }

            if (ch == 'u' && !moves.isEmpty()) {
                undo();
                continue;
            }

            if (ch == 'q') {
                break;
            }

            final int take = ch - '0';

            couldTake = take(take);

            if(isGameOver()){
                winner = PLAYER_WON;
                break;
            }

            move.playerTake = take;

            // Only computer play if we could
            if (couldTake) {
                move.cpuTake = cpuTurn();
                out.format("The computer takes %d.\n", move.cpuTake);

                moves.push(move);
            }

        } while (!couldTake || !isGameOver());
        printVictoryMessage(out, winner);
    }

    private static void printVictoryMessage(PrintStream out, int winner) {
        final String msg = victoryMessage(winner);
        if (msg != null) {
            out.println(msg);
        }
    }

    private static String victoryMessage(int winner) {
        return switch (winner) {
            case PLAYER_WON -> "Player has won!";
            case CPU_WON -> "CPU has won!";
            default -> null;
        };
    }

    private static PrintStream printify(OutputStream out) {
        if(out instanceof PrintStream)
            return (PrintStream) out;
        else
            return new PrintStream(out);
    }

    private static char getValidInputChar(Scanner _in) {
        return getValidInput(_in).charAt(0);
    }

    private static String getValidInput(Scanner _in) {
        String input;
        do {
            input = _in.nextLine().toLowerCase();
        } while (input.isEmpty());
        return input;
    }

    private void undo() {
        Move last = moves.pop();
        n += last.cpuTake;
        n += last.playerTake;
    }

    @Override
    public String toString() {
        char[] str = new char[n];
        Arrays.fill(str, 'O');

        return "%s\n".formatted(new String(str));
    }
}
