package unit1;

import unit1.exceptions.LinterException;
import unit1.exceptions.MissingClosureException;
import unit1.exceptions.UnexpectedClosureException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Linter {
    private static LinterException lex;

    private enum OpenClose {
        ANGLE("<", ">"),
        COMMENT("/*", "*/"),
        CURLY("{", "}"),
        PAREN("(", ")"),
        SQUARE("[", "]"),
        ;

        private final String open;
        private final String close;

        private static Pattern allOpen;
        private static Pattern allClose;
        private static Pattern allPattern;

        private static void ensureOpenPattern() {
            if(allOpen != null) return;

            StringBuilder sb = new StringBuilder();

            for (OpenClose oc : values()) {
                sb.append("(").append(Pattern.quote(oc.open)).append(")|");
            }

            // Ignore the last character, which will be a useless pipe '|'
            final String pattern = sb.substring(0, sb.length() - 1);
            allOpen = Pattern.compile(pattern);
        }

        private static void ensureClosePattern() {
            if(allClose != null) return;

            StringBuilder sb = new StringBuilder();

            for (OpenClose oc : values()) {
                sb.append("(").append(Pattern.quote(oc.close)).append(")|");
            }

            // Ignore the last character, which will be a useless pipe '|'
            final String pattern = sb.substring(0, sb.length() - 1);
            allClose = Pattern.compile(pattern);
        }

        private OpenClose(String open, String close) {
            this.open = open;
            this.close = close;
        }

        public static OpenClose getByOpen(char ch) {
            return getByOpen(Character.toString(ch));
        }

        public static OpenClose getByOpen(CharSequence open) {
            if (open == null) return null;

            for (OpenClose oc : values()) {
                if (oc.open.contentEquals(open))
                    return oc;
            }

            return null;
        }

        public static List<OpenCloseToken> getAllByOpen(CharSequence opens) {
            ensureOpenPattern();

            ArrayList<OpenCloseToken> list = new ArrayList<>();

            allOpen.matcher(opens).results().forEachOrdered(matchResult -> {
                final String group = matchResult.group();
                if(group != null)
                    list.add(new OpenCloseToken(getByOpen(group), true));
            });

            return list;
        }

        public static OpenClose getByClose(char ch) {
            return getByClose(Character.toString(ch));
        }

        public static OpenClose getByClose(CharSequence close) {
            if (close == null) return null;

            for (OpenClose oc : values()) {
                if (oc.close.contentEquals(close))
                    return oc;
            }

            return null;
        }

        public static List<OpenCloseToken> getAllByClose(CharSequence closes) {
            ensureClosePattern();

            ArrayList<OpenCloseToken> list = new ArrayList<>();

            allClose.matcher(closes).results().forEachOrdered(matchResult -> {
                final String group = matchResult.group();
                if(group != null)
                    list.add(new OpenCloseToken(getByClose(group), false));
            });

            return list;
        }

        public static List<OpenCloseToken> getAll(CharSequence input) {
            ensureAllPattern();
            ArrayList<OpenCloseToken> list = new ArrayList<>();

            allPattern.matcher(input).results().forEachOrdered(matchResult -> {
                final String group = matchResult.group();
                if(group != null) {
                    OpenClose potentialOpenOc = getByOpen(group);

                    if(potentialOpenOc != null){
                        list.add(new OpenCloseToken(potentialOpenOc, true));
                    } else {
                        OpenClose closeOc = getByClose(group);
                        list.add(new OpenCloseToken(closeOc, false));

                    }
                }
            });

            return list;
        }

        private static void ensureAllPattern() {
            if(allPattern != null) return;

            ensureOpenPattern();
            ensureClosePattern();

            allPattern = Pattern.compile(allOpen.pattern() + "|" + allClose.pattern());
        }
    }

    private record OpenCloseToken(OpenClose openClose, boolean opens) {
        public boolean closes() {
            return !opens;
        }
    }

    /**
     * Lints the contents of a file.
     *
     * @param file The file to read and lint.
     * @throws FileNotFoundException If the file could not be loaded.
     */
    public static void lint(File file) throws FileNotFoundException {
        lint(new FileInputStream(file));
    }

    /**
     * Lint a stream of strings treated as individual lines.
     * Will terminate the stream.
     * <p>
     * If new line characters ({@code \n}) are present inside
     * lines of the given stream, reported line number values
     * may be inaccurate.
     *
     * @param lines A stream of lines of text to lint
     */
    public static void lint(Stream<String> lines) {
        Stack<OpenClose> stack = new Stack<>();

        Iterator<String> it = lines.iterator();

        int lineNumber = 0;

        while(it.hasNext()){
            String line = it.next();

            lint(line, stack, lineNumber++);
        }

        assureAllClosed(stack);
        throwLex();
    }

    /**
     * Lints the results of the given input stream.
     *
     * @param stream Where to take input to lint.
     */
    public static void lint(InputStream stream) {
        Stack<OpenClose> stack = new Stack<>();
        Scanner scan = new Scanner(stream);

        int lineNumber = 0;
        while(scan.hasNextLine()) {
            lineNumber++;
            String token = scan.nextLine();

            lint(token, stack, lineNumber);
        }

        assureAllClosed(stack);
        throwLex();
    }

    /**
     * Lints each line of the given input string.
     *
     * @param input The string to lint.
     */
    public static void lint(String input) {
        Stack<OpenClose> stack = new Stack<>();
        String[] lines = input.split("\n");

        for (int i = 0; i < lines.length; i++) {
            lint(lines[i], stack, i);
        }

        assureAllClosed(stack);
        throwLex();
    }

    /**
     * Helper method, lints individual lines.
     *
     * @param token The line to lint.
     * @param stack The stack for the current block of text.
     * @param lineNumber The line number; used for error messages.
     */
    private static void lint(String token, Stack<OpenClose> stack, int lineNumber) {
        List<OpenCloseToken> list = OpenClose.getAll(token);

        for (OpenCloseToken ocToken : list) {
            if(ocToken != null)
                check(stack, lineNumber, ocToken);
        }
    }

    private static void check(Stack<OpenClose> stack, int lineNumber, OpenCloseToken ocToken) {
        if(ocToken.opens()) {
            stack.push(ocToken.openClose());
        } else {
            if(stack.isEmpty()) {
                extraClose(lineNumber, ocToken);
            }
            
            final OpenClose last = stack.pop();
            if (last != ocToken.openClose()) {
                wrongClose(lineNumber, ocToken, last);
            }
        }
    }

    private static void assureAllClosed(Stack<OpenClose> stack) {
        if(stack.isEmpty()) return;

        ensureLex();

        StringBuilder neededClosures = new StringBuilder();
        while(!stack.isEmpty()) {
            neededClosures.append(stack.pop().close).append(" ");
        }

        lex.also(new MissingClosureException("Expected " + neededClosures));
    }

    private static void extraClose(int lineNumber, OpenCloseToken badOc) {
        ensureLex();
        lex.also(new UnexpectedClosureException(badOc.openClose().close, lineNumber));
    }

    private static void wrongClose(int lineNumber, OpenCloseToken unexpected, OpenClose expected) {
        ensureLex();
        lex.also(new UnexpectedClosureException(unexpected.openClose().close, expected.close, lineNumber));
    }

    private static void ensureLex() {
        if (lex == null) {
            lex = new LinterException();
        }
    }

    private static void throwLex() {
        // ignore lack of problems
        if(lex == null) return;

        // Let garbage collection take lex as needed.
        LinterException temp = lex;
        lex = null;
        throw temp;
    }
}
