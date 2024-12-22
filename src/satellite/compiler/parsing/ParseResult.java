package satellite.compiler.parsing;

public class ParseResult<T> {
    private boolean success = false;
    private final T value;
    private final String remainder;
    private final String message;
    private final int line;
    private final int column;

    private ParseResult(boolean success, T value, String remainder, String message, int line, int column) {
        this.success = success;
        this.value = value;
        this.remainder = remainder;
        this.message = message;
        this.line = line;
        this.column = column;
    }

    public static <T> ParseResult<T> pass(T value, String remainder, int line, int column) {
        return new ParseResult<>(true, value, remainder, null, line, column);
    }

    public static <T> ParseResult<T> fail(String message, int line, int column) {
        return new ParseResult<>(false, null, null, message, line, column);
    }

    public static <T> ParseResult<T> latest(ParseResult<T> first, ParseResult<T> second) {
        if (first.getLine() > second.getLine()) {
            return first;
        } else if (first.getLine() == second.getLine()) {
            if (first.getColumn() >= second.getColumn()) {
                return first;
            } else {
                return second;
            }
        } else {
            return second;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public T getValue() {
        return value;
    }

    public String getRemainder() {
        return remainder;
    }

    public String getMessage() {
        return message;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        if (isSuccess()) {
            if (remainder.isEmpty()) {
                return String.format("[PASS] %s", value);
            } else {
                return String.format("[PASS] %s (+ %s)", value, remainder);
            }
        } else {
            return String.format("[FAIL] %d:%d: %s", line, column, message);
        }
    }
}
