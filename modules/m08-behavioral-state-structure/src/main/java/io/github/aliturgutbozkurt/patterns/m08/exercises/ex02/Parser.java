package io.github.aliturgutbozkurt.patterns.m08.exercises.ex02;

import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.Binary;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.Bool;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.If;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.Num;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.Unary;
import io.github.aliturgutbozkurt.patterns.m08.exercises.ex02.Expr.Var;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * GIVEN — do not modify. A complete recursive-descent parser for the mini language. Errors throw
 * {@link IllegalArgumentException} with the 1-based column; nesting is capped at 200.
 *
 * <pre>
 * expression := "if" expression "then" expression "else" expression | or
 * or         := and ("or" and)*
 * and        := not ("and" not)*
 * not        := "not" not | comparison
 * comparison := sum (("=" | "<" | "<=") sum)?          -- comparisons do not chain
 * sum        := product (("+" | "-") product)*
 * product    := unary (("*" | "/") unary)*
 * unary      := "-" unary | primary
 * primary    := NUMBER | "true" | "false" | NAME | "(" expression ")"
 * </pre>
 */
public final class Parser {

    private static final int MAX_NESTING = 200;
    private static final Set<String> KEYWORDS = Set.of("if", "then", "else", "and", "or", "not", "true", "false");
    private static final Map<String, BinaryOp> COMPARISONS = Map.of("=", BinaryOp.EQ, "<", BinaryOp.LT, "<=",
            BinaryOp.LE);

    private enum Kind { NUMBER, WORD, SYMBOL, END }

    private record Tok(Kind kind, String text, int column) {}

    private final List<Tok> tokens;
    private int position;
    private int nesting;

    private Parser(String source) {
        this.tokens = tokenize(source);
    }

    public static Expr parse(String source) {
        var parser = new Parser(source);
        Expr expr = parser.expression();
        Tok rest = parser.peek();
        if (rest.kind() != Kind.END) {
            throw error("unexpected '" + rest.text() + "'", rest);
        }
        return expr;
    }

    private Expr expression() {
        if (at("if")) {
            Tok start = advance();
            enter(start);
            Expr condition = expression();
            expect("then");
            Expr then = expression();
            expect("else");
            Expr otherwise = expression();
            nesting--;
            return new If(condition, then, otherwise);
        }
        return or();
    }

    private Expr or() {
        Expr left = and();
        while (at("or")) {
            advance();
            left = new Binary(BinaryOp.OR, left, and());
        }
        return left;
    }

    private Expr and() {
        Expr left = not();
        while (at("and")) {
            advance();
            left = new Binary(BinaryOp.AND, left, not());
        }
        return left;
    }

    private Expr not() {
        if (at("not")) {
            enter(advance());
            Expr operand = not();
            nesting--;
            return new Unary(UnaryOp.NOT, operand);
        }
        return comparison();
    }

    private Expr comparison() {
        Expr left = sum();
        BinaryOp op = COMPARISONS.get(peek().text());
        if (op == null || peek().kind() != Kind.SYMBOL) {
            return left;
        }
        advance();
        Expr result = new Binary(op, left, sum());
        if (peek().kind() == Kind.SYMBOL && COMPARISONS.containsKey(peek().text())) {
            throw error("comparisons do not chain", peek());
        }
        return result;
    }

    private Expr sum() {
        Expr left = product();
        while (atSymbol("+") || atSymbol("-")) {
            BinaryOp op = advance().text().equals("+") ? BinaryOp.ADD : BinaryOp.SUB;
            left = new Binary(op, left, product());
        }
        return left;
    }

    private Expr product() {
        Expr left = unary();
        while (atSymbol("*") || atSymbol("/")) {
            BinaryOp op = advance().text().equals("*") ? BinaryOp.MUL : BinaryOp.DIV;
            left = new Binary(op, left, unary());
        }
        return left;
    }

    private Expr unary() {
        if (atSymbol("-")) {
            enter(advance());
            Expr operand = unary();
            nesting--;
            return new Unary(UnaryOp.NEG, operand);
        }
        return primary();
    }

    private Expr primary() {
        Tok tok = advance();
        if (tok.kind() == Kind.NUMBER) {
            try {
                return new Num(Long.parseLong(tok.text()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("number too large at column " + tok.column(), e);
            }
        }
        if (tok.kind() == Kind.WORD && (tok.text().equals("true") || tok.text().equals("false"))) {
            return new Bool(tok.text().equals("true"));
        }
        if (tok.kind() == Kind.WORD && !KEYWORDS.contains(tok.text())) {
            return new Var(tok.text());
        }
        if (tok.kind() == Kind.SYMBOL && tok.text().equals("(")) {
            enter(tok);
            Expr inner = expression();
            if (!atSymbol(")")) {
                throw error("expected ')'", peek());
            }
            advance();
            nesting--;
            return inner;
        }
        throw error("expected a value", tok);
    }

    private void enter(Tok at) {
        if (++nesting > MAX_NESTING) {
            throw error("nesting deeper than " + MAX_NESTING, at);
        }
    }

    private void expect(String keyword) {
        if (!at(keyword)) {
            throw error("expected '" + keyword + "'", peek());
        }
        advance();
    }

    private boolean at(String keyword) {
        return peek().kind() == Kind.WORD && peek().text().equals(keyword);
    }

    private boolean atSymbol(String symbol) {
        return peek().kind() == Kind.SYMBOL && peek().text().equals(symbol);
    }

    private Tok peek() {
        return tokens.get(position);
    }

    private Tok advance() {
        Tok tok = tokens.get(position);
        if (position < tokens.size() - 1) {
            position++;
        }
        return tok;
    }

    private static IllegalArgumentException error(String problem, Tok at) {
        return new IllegalArgumentException(problem + " at column " + at.column());
    }

    private static List<Tok> tokenize(String source) {
        var tokens = new ArrayList<Tok>();
        int i = 0;
        while (i < source.length()) {
            char c = source.charAt(i);
            int start = i;
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }
            Kind kind;
            if (c >= '0' && c <= '9') {
                while (i < source.length() && source.charAt(i) >= '0' && source.charAt(i) <= '9') {
                    i++;
                }
                kind = Kind.NUMBER;
            } else if (Character.isLetter(c) || c == '_') {
                while (i < source.length() && (Character.isLetterOrDigit(source.charAt(i)) || source.charAt(i) == '_')) {
                    i++;
                }
                kind = Kind.WORD;
            } else if (source.startsWith("<=", i)) {
                i += 2;
                kind = Kind.SYMBOL;
            } else if ("+-*/()=<".indexOf(c) >= 0) {
                i++;
                kind = Kind.SYMBOL;
            } else {
                throw new IllegalArgumentException("unexpected character '" + c + "' at column " + (i + 1));
            }
            tokens.add(new Tok(kind, source.substring(start, i), start + 1));
        }
        tokens.add(new Tok(Kind.END, "end", source.length() + 1));
        return List.copyOf(tokens);
    }
}
