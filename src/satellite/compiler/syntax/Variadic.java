package satellite.compiler.syntax;

import satellite.compiler.parsing.Parser;

import java.util.List;

public class Variadic {
    private final List<Expression> expressions;

    public Variadic(List<Expression> expressions) {
        this.expressions = expressions;
    }

    @Override
    public String toString() {
        return expressions.toString();
    }

    public static Parser<Variadic> parser() {
        return Expression.parser().bind(expression -> Parser.symbol(",").then(Parser.pure(expression.getValue()))).some().bind(expressions -> Expression.parser().bind(last -> {
            expressions.getValue().add(last.getValue());
            return Parser.pure(new Variadic(expressions.getValue()));
        })).or(Parser.pure(new Variadic(List.of())));
    }
}
