package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc;

/**
 * The source text is not a valid program; the message ends with the 1-based column of the problem.
 *
 * @see "m08 lesson, section Interpreter — Modern Java 27"
 */
public final class ParseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int column;

    /** E.g. {@code new ParseException("expected ')'", 8)} has the message {@code expected ')' at column 8}. */
    public ParseException(String problem, int column) {
        this(problem, column, null);
    }

    public ParseException(String problem, int column, Throwable cause) {
        super(problem + " at column " + column, cause);
        this.column = column;
    }

    private ParseException(ParseException original, int line) {
        super("line " + line + ": " + original.getMessage(), original);
        this.column = original.column;
    }

    /** The same error, prefixed with the program line it came from. */
    public ParseException onLine(int line) {
        return new ParseException(this, line);
    }

    public int column() {
        return column;
    }
}
