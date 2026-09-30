package io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc;

import io.github.aliturgutbozkurt.patterns.m08.examples.interpreter.calc.Token.Symbol;
import java.util.ArrayList;
import java.util.List;

/**
 * Text → tokens. Numbers are non-negative decimal literals (a leading {@code -} is the unary operator); names start
 * with a letter; {@code let} and {@code in} are keywords. The list always ends with {@link Token.End}.
 *
 * @see "m08 lesson, section Interpreter — Modern Java 27"
 */
public final class Lexer {

    private Lexer() {}

    public static List<Lexeme> tokenize(String source) {
        var lexemes = new ArrayList<Lexeme>();
        int i = 0;
        while (i < source.length()) {
            char c = source.charAt(i);
            int column = i + 1;
            if (Character.isWhitespace(c)) {
                i++;
            } else if (isDigit(c)) {
                int start = i;
                while (i < source.length() && isDigit(source.charAt(i))) {
                    i++;
                }
                lexemes.add(new Lexeme(new Token.Number(parseNumber(source.substring(start, i), column)), column));
            } else if (isLetter(c)) {
                int start = i;
                while (i < source.length() && (isLetter(source.charAt(i)) || isDigit(source.charAt(i)))) {
                    i++;
                }
                String word = source.substring(start, i);
                Token token = word.equals("let") || word.equals("in") ? new Token.Keyword(word) : new Token.Ident(word);
                lexemes.add(new Lexeme(token, column));
            } else {
                lexemes.add(new Lexeme(symbol(c, column), column));
                i++;
            }
        }
        lexemes.add(new Lexeme(new Token.End(), source.length() + 1));
        return List.copyOf(lexemes);
    }

    private static Symbol symbol(char c, int column) {
        return switch (c) {
            case '+' -> Symbol.PLUS;
            case '-' -> Symbol.MINUS;
            case '*' -> Symbol.STAR;
            case '/' -> Symbol.SLASH;
            case '(' -> Symbol.LPAREN;
            case ')' -> Symbol.RPAREN;
            case '=' -> Symbol.EQUALS;
            default -> throw new ParseException("unexpected character '" + c + "'", column);
        };
    }

    private static long parseNumber(String digits, int column) {
        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException e) {
            throw new ParseException("number too large", column, e);
        }
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }
}
