package satellite.compiler.syntax;

import satellite.compiler.parsing.Parser;

import java.util.ArrayList;
import java.util.List;

public abstract class Program {
    public static class ProgramBlock extends Program {
        private final List<Program> programs;

        public ProgramBlock(List<Program> programs) {
            this.programs = programs;
        }

        @Override
        public String toString() {
            return String.format("do %s", programs.toString());
        }
    }

    public static class ProgramAssignment extends Program {
        private final boolean localDeclaration;
        private final String identifier;
        private final Expression expression;

        private ProgramAssignment(boolean localDeclaration, String identifier, Expression expression) {
            this.localDeclaration = localDeclaration;
            this.identifier = identifier;
            this.expression = expression;
        }

        public ProgramAssignment(String identifier, Expression expression) {
            this.localDeclaration = false;
            this.identifier = identifier;
            this.expression = expression;
        }

        public ProgramAssignment asLocalDeclaration() {
            return new ProgramAssignment(true, identifier, expression);
        }

        @Override
        public String toString() {
            if (localDeclaration) {
                return String.format("init local %s = %s", identifier, expression);
            } else {
                return String.format("set %s = %s", identifier, expression);
            }
        }
    }

    public static class ProgramCall extends Program {
        private final String identifier;
        private final Variadic variadic;

        public ProgramCall(String identifier, Variadic variadic) {
            this.identifier = identifier;
            this.variadic = variadic;
        }

        @Override
        public String toString() {
            return String.format("call %s with %s", identifier, variadic);
        }
    }

    public static class ProgramIfElse extends Program {

    }

    public static class ProgramIf extends Program {

    }

    public static class ProgramWhile extends Program {

    }

    public static Parser<Program> parser() {
        return blockParser()
                .or(localAssignmentParser())
                .or(assignmentParser())
                .or(callParser())
                .or(ifElseParser())
                .or(ifParser())
                .or(whileParser());
    }

    public static Parser<Program> blockParser() {
        return Parser.keyword("do").lazy(() -> parser().some()).bind(result -> Parser.symbol("end").then(Parser.pure(new ProgramBlock(result.getValue()))));
    }

    public static Parser<Program> localAssignmentParser() {
        return Parser.keyword("local").then(assignmentParser().map(assignment -> ((ProgramAssignment) assignment).asLocalDeclaration()));
    }

    public static Parser<Program> assignmentParser() {
        return Parser.identifier().bind(identifier -> Parser.symbol("=").then(Expression.parser()).bind(expression -> Parser.pure(new ProgramAssignment(identifier.getValue(), expression.getValue()))));
    }

    public static Parser<Program> callParser() {
        return Parser.identifier().bind(identifier -> Parser.symbol("(").then(Variadic.parser()).bind(variadic -> Parser.symbol(")").then(Parser.pure(new ProgramCall(identifier.getValue(), variadic.getValue())))));
    }

    public static Parser<Program> ifElseParser() {
        return Parser.error("TODO");
    }

    public static Parser<Program> ifParser() {
        return Parser.error("TODO");
    }

    public static Parser<Program> whileParser() {
        return Parser.error("TODO");
    }
}
