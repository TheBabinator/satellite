package satellite.compiler.parsing;

import satellite.compiler.SatelliteSyntaxException;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Parser<T> {
    ParseResult<T> parse(String input, int line, int column);

    default T parseTotal(String input) throws SatelliteSyntaxException {
        ParseResult<T> result = parse(input, 1, 1);
        if (result.isSuccess()) {
            if (result.getRemainder().isEmpty()) {
                return result.getValue();
            } else {
                throw new SatelliteSyntaxException("%d:%d: expected EOF", result.getLine(), result.getColumn());
            }
        } else {
            throw new SatelliteSyntaxException("%d:%d: %s", result.getLine(), result.getColumn(), result.getMessage());
        }
    }

    static <T> Parser<T> pure(T value) {
        return (input, line, column) -> ParseResult.pass(value, input, line, column);
    }

    static <T> Parser<T> impure(T value, String format, Object... arguments) {
        return (input, line, column) -> {
            System.out.printf(format, arguments);
            return ParseResult.pass(value, input, line, column);
        };
    }

    static <T> Parser<T> error(String message) {
        return (input, line, column) -> ParseResult.fail(message, line, column);
    }

    static Parser<Character> item() {
        return (input, line, column) -> {
            if (!input.isEmpty()) {
                char next = input.charAt(0);
                if (next == '\n') {
                    return ParseResult.pass(next, input.substring(1), line + 1, 1);
                } else if (next == '\t') {
                    return ParseResult.pass(next, input.substring(1), line, column + 4);
                } else if (Character.isISOControl(next)) {
                    return ParseResult.pass(next, input.substring(1), line, column);
                } else {
                    return ParseResult.pass(next, input.substring(1), line, column + 1);
                }
            } else {
                return ParseResult.fail("unexpected EOF", line, column);
            }
        };
    }

    static Parser<String> string(String string) {
        Parser<Void> parser = pure(null);
        for (char c : string.toCharArray()) {
            parser = parser.then(item().whereIs(c).toVoid());
        }
        return parser.then(pure(string));
    }

    static Parser<String> optionalSpace() {
        return item().where(Character::isWhitespace).some().map(STRINGIFY);
    }

    static Parser<String> requiredSpace() {
        return item().where(Character::isWhitespace).many().map(STRINGIFY);
    }

    static Parser<String> identifier() {
        return optionalSpace()
                .then(item().where(Character::isAlphabetic).or(item().whereIs('_')))
                .bind(first -> item().where(Character::isLetterOrDigit).or(item().whereIs('_')).some().map(STRINGIFY).map(rest -> first.getValue() + rest))
                .bind(identifier -> optionalSpace().then(pure(identifier.getValue())));
    }

    static Parser<String> symbol(String symbol) {
        return optionalSpace().then(string(symbol)).then(optionalSpace()).then(pure(symbol));
    }

    static Parser<String> keyword(String symbol) {
        return optionalSpace().then(string(symbol)).then(requiredSpace()).then(pure(symbol));
    }

    default Parser<T> or(Parser<T> other) {
        return (input, line, column) -> {
            ParseResult<T> result = parse(input, line, column);
            if (result.isSuccess()) {
                return result;
            } else {
                ParseResult<T> alternativeResult = other.parse(input, line, column);
                if (alternativeResult.isSuccess()) {
                    return alternativeResult;
                } else {
                    return ParseResult.latest(result, alternativeResult);
                }
            }
        };
    }

    default <U> Parser<U> then(Parser<U> next) {
        return (input, line, column) -> {
            ParseResult<T> result = parse(input, line, column);
            if (result.isSuccess()) {
                return next.parse(result.getRemainder(), result.getLine(), result.getColumn());
            } else {
                return ParseResult.fail(result.getMessage(), result.getLine(), result.getColumn());
            }
        };
    }

    default <U> Parser<U> lazy(Supplier<Parser<U>> nextSupplier) {
        return (input, line, column) -> {
            ParseResult<T> result = parse(input, line, column);
            if (result.isSuccess()) {
                return nextSupplier.get().parse(result.getRemainder(), result.getLine(), result.getColumn());
            } else {
                return ParseResult.fail(result.getMessage(), result.getLine(), result.getColumn());
            }
        };
    }

    default <U> Parser<U> bind(Function<ParseResult<T>, Parser<U>> nextFunction) {
        return (input, line, column) -> {
            ParseResult<T> result = parse(input, line, column);
            if (result.isSuccess()) {
                return nextFunction.apply(result).parse(result.getRemainder(), result.getLine(), result.getColumn());
            } else {
                return ParseResult.fail(result.getMessage(), result.getLine(), result.getColumn());
            }
        };
    }

    default <U> Parser<U> map(Function<T, U> function) {
        return (input, line, column) -> {
            ParseResult<T> result = parse(input, line, column);
            if (result.isSuccess()) {
                return ParseResult.pass(function.apply(result.getValue()), result.getRemainder(), result.getLine(), result.getColumn());
            } else {
                return ParseResult.fail(result.getMessage(), result.getLine(), result.getColumn());
            }
        };
    }

    default Parser<T> mapError(Function<String, String> function) {
        return (input, line, column) -> {
            ParseResult<T> result = parse(input, line, column);
            if (result.isSuccess()) {
                return result;
            } else {
                return ParseResult.fail(function.apply(result.getMessage()), result.getLine(), result.getColumn());
            }
        };
    }

    default Parser<T> withError(String message) {
        return (input, line, column) -> {
            ParseResult<T> result = parse(input, line, column);
            if (result.isSuccess()) {
                return result;
            } else {
                return ParseResult.fail(message, result.getLine(), result.getColumn());
            }
        };
    }

    default Parser<T> where(Predicate<T> predicate) {
        return (input, line, column) -> {
            ParseResult<T> result = parse(input, line, column);
            if (result.isSuccess()) {
                if (predicate.test(result.getValue())) {
                    return result;
                } else {
                    return ParseResult.fail("predicate failed", line, column);
                }
            } else {
                return result;
            }
        };
    }

    default Parser<T> whereIs(T value) {
        return where(x -> x == value);
    }

    default Parser<Void> toVoid() {
        return map(x -> null);
    }

    default Parser<List<T>> many() {
        return bind(result -> many().or(pure(new LinkedList<>())).map(values -> {
            values.add(0, result.getValue());
            return values;
        }));
    }

    default Parser<List<T>> some() {
        return many().or(pure(new LinkedList<>()));
    }

    Function<List<Character>, String> STRINGIFY = characters -> {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : characters) {
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    };
}
