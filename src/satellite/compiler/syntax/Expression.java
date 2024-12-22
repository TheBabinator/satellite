package satellite.compiler.syntax;

import satellite.compiler.parsing.Parser;

import java.util.function.Predicate;

public abstract class Expression {
    public static class ExpressionNumber extends Expression {
        private final int value;

        public ExpressionNumber(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }
    }

    public static Parser<Expression> parser() {
        return numberParser();
    }

    public static Parser<Expression> numberParser() {
        return Parser.item().where(Character::isDigit).many().map(Parser.STRINGIFY).bind(digits -> {
            try {
                return Parser.pure(new ExpressionNumber(Integer.parseInt(digits.getValue())));
            } catch (NumberFormatException numberFormatException) {
                return Parser.error("not a number");
            }
        });
    }
}
