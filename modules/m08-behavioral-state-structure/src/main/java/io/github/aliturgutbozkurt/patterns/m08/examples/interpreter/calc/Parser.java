package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc;

import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Binary;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Let;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Neg;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Num;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Expr.Var;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Token.End;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Token.Ident;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Token.Keyword;
import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Token.Symbol;
import java.util.List;
import java.util.Objects;

/**
 * Recursive-descent parser: one method per grammar rule, lowest precedence first. Loops make {@code +}/{@code -} and
 * {@code *}/{@code /} left-associative. Both the nesting of the input and the depth of the resulting tree are capped
 * at {@value #MAX_DEPTH}, so neither this parser nor the recursive {@link Evaluator} can overflow the stack.
 *
 * <pre>
 * expression := "let" NAME "=" expression "in" expression | sum
 * sum        := product (("+" | "-") product)*
 * product    := unary (("*" | "/") unary)*
 * unary      := "-" unary | primary
 * primary    := NUMBER | NAME | "(" expression ")"
 * </pre>
 *
 * @see "m08 lesson, section Interpreter — Modern Java 27"
 */
public final class Parser {

    public static final int MAX_DEPTH = 200;

    /**
     * {@code let name = value}, one line of a {@link Program}.
     *
     * @see "m08 lesson, section Interpreter — Modern Java 27"
     */
    public record Binding(String name, Expr value) {

        public Binding {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(value, "value");
        }
    }

    /** A parsed subtree and its depth (a leaf has depth 1). */
    private record Node(Expr expr, int depth) {}

    private final List<Lexeme> lexemes;
    private int position;
    private int nesting;

    private Parser(String source) {
        this.lexemes = Lexer.tokenize(source);
    }

    public static Expr parse(String source) {
        var parser = new Parser(source);
        Node result = parser.expression();
        parser.expectEnd();
        return result.expr();
    }

    /** Parses one {@code let name = expression} line. */
    public static Binding parseBinding(String source) {
        var parser = new Parser(source);
        parser.expectKeyword("let");
        String name = parser.name();
        parser.expect(Symbol.EQUALS, "expected '='");
        Node value = parser.expression();
        parser.expectEnd();
        return new Binding(name, value.expr());
    }

    private Node expression() {
        if (peek().token() instanceof Keyword(var word) && word.equals("let")) {
            Lexeme let = advance();
            enter(let);
            String name = name();
            expect(Symbol.EQUALS, "expected '='");
            Node value = expression();
            expectKeyword("in");
            Node body = expression();
            nesting--;
            return node(new Let(name, value.expr(), body.expr()), value, body, let);
        }
        return sum();
    }

    private Node sum() {
        Node left = product();
        while (peek().token() == Symbol.PLUS || peek().token() == Symbol.MINUS) {
            Lexeme operator = advance();
            Op op = operator.token() == Symbol.PLUS ? Op.ADD : Op.SUB;
            Node right = product();
            left = node(new Binary(op, left.expr(), right.expr()), left, right, operator);
        }
        return left;
    }

    private Node product() {
        Node left = unary();
        while (peek().token() == Symbol.STAR || peek().token() == Symbol.SLASH) {
            Lexeme operator = advance();
            Op op = operator.token() == Symbol.STAR ? Op.MUL : Op.DIV;
            Node right = unary();
            left = node(new Binary(op, left.expr(), right.expr()), left, right, operator);
        }
        return left;
    }

    private Node unary() {
        if (peek().token() == Symbol.MINUS) {
            Lexeme minus = advance();
            enter(minus);
            Node operand = unary();
            nesting--;
            return node(new Neg(operand.expr()), operand, operand, minus);
        }
        return primary();
    }

    private Node primary() {
        Lexeme lexeme = advance();
        return switch (lexeme.token()) {
            case Token.Number(var value) -> new Node(new Num(value), 1);
            case Ident(var name) -> new Node(new Var(name), 1);
            case Symbol.LPAREN -> {
                enter(lexeme);
                Node inner = expression();
                expect(Symbol.RPAREN, "expected ')'");
                nesting--;
                yield inner;
            }
            case Symbol _, Keyword _, End _ -> throw new ParseException("expected a number, a name or '('",
                    lexeme.column());
        };
    }

    private Node node(Expr expr, Node first, Node second, Lexeme at) {
        int depth = 1 + Math.max(first.depth(), second.depth());
        if (depth > MAX_DEPTH) {
            throw new ParseException("nesting deeper than " + MAX_DEPTH, at.column());
        }
        return new Node(expr, depth);
    }

    private void enter(Lexeme at) {
        if (++nesting > MAX_DEPTH) {
            throw new ParseException("nesting deeper than " + MAX_DEPTH, at.column());
        }
    }

    private String name() {
        Lexeme lexeme = advance();
        if (lexeme.token() instanceof Ident(var name)) {
            return name;
        }
        throw new ParseException("expected a name", lexeme.column());
    }

    private void expect(Symbol symbol, String problem) {
        Lexeme lexeme = advance();
        if (lexeme.token() != symbol) {
            throw new ParseException(problem, lexeme.column());
        }
    }

    private void expectKeyword(String word) {
        Lexeme lexeme = advance();
        if (!(lexeme.token() instanceof Keyword(var found) && found.equals(word))) {
            throw new ParseException("expected '" + word + "'", lexeme.column());
        }
    }

    private void expectEnd() {
        Lexeme lexeme = peek();
        if (!(lexeme.token() instanceof End)) {
            throw new ParseException("unexpected '" + lexeme.token().text() + "'", lexeme.column());
        }
    }

    private Lexeme peek() {
        return lexemes.get(position);
    }

    /** Consumes one lexeme; never moves past the final {@code End}. */
    private Lexeme advance() {
        Lexeme lexeme = lexemes.get(position);
        if (position < lexemes.size() - 1) {
            position++;
        }
        return lexeme;
    }
}
