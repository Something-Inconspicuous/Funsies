package unit1.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class LinterException extends RuntimeException {
    private final List<LinterException> problems;

    public LinterException() {
        this(new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    public LinterException(List<? extends LinterException> existing) {
        problems = (List<LinterException>) existing;
    }

    protected LinterException(String message) {
        super(message);
        problems = null;
    }

    public final LinterException also(LinterException ex) {
        if (problems != null) problems.add(ex);
        return this;
    }

    @Override
    public void printStackTrace(PrintWriter out) {
        if(problems == null){
            superPrintStackTrace(out);
        } else {
            for (LinterException ex : problems) {
                ex.superPrintStackTrace(out);
            }
        }
    }

    @Override
    public void printStackTrace(PrintStream out) {
        if(problems == null){
            superPrintStackTrace(out);
        } else {
            for (LinterException ex : problems) {
                ex.superPrintStackTrace(out);
            }
        }
    }

    private void superPrintStackTrace(PrintWriter s) {
        super.printStackTrace(s);
    }

    private void superPrintStackTrace(PrintStream s) {
        super.printStackTrace(s);
    }
}
