package unit1.exceptions;

public class MissingClosureException extends LinterException {
    public MissingClosureException(String message) {
        super(message);
    }

    public MissingClosureException(String missing, int lineNumber) {
        super("Expected " + missing + " on line " + lineNumber);
    }
}
