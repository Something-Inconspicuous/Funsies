package unit1.exceptions;

public class UnexpectedClosureException extends LinterException {
    public UnexpectedClosureException(String message) {
        super(message);
    }

    public UnexpectedClosureException(String unexpected, int lineNumber) {
        super("Unexpected " + unexpected + " on line " + lineNumber + ".");
    }

    public UnexpectedClosureException(String unexpected, String expected, int lineNumber) {
        super("Unexpected " + unexpected + " on line " + lineNumber + "; expected " + expected);
    }
}
