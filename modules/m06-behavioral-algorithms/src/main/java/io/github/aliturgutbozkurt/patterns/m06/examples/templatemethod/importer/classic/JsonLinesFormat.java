package io.github.aliturgutbozkurt.patterns.m06.examples.templatemethod.importer.classic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses JSON Lines where every line is a <em>flat</em> object of strings and numbers, e.g.
 * {@code {"sku":"A-1","price":1.20}}. Numbers are kept as text so {@code 1.20} stays {@code 1.20}. A real project
 * would use a JSON library; {@code src/main} has no dependencies, so this hand-written parser is enough here.
 *
 * @see "m06 lesson, section Template Method"
 */
public final class JsonLinesFormat {

    private JsonLinesFormat() {}

    /** @throws IllegalArgumentException with the line number, if a line is not a flat JSON object */
    public static List<RawRow> parse(String input) {
        List<String> lines = input.lines().toList();
        List<RawRow> rows = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                rows.add(new RawRow(i + 1, new FlatObjectParser(lines.get(i), i + 1).parseObject()));
            }
        }
        return rows;
    }

    /** A tiny recursive-descent parser for one line; not thread-safe, one instance per line. */
    private static final class FlatObjectParser {

        private final String text;
        private final int lineNumber;
        private int pos;

        FlatObjectParser(String text, int lineNumber) {
            this.text = text;
            this.lineNumber = lineNumber;
        }

        Map<String, String> parseObject() {
            Map<String, String> fields = new LinkedHashMap<>();
            expect('{');
            if (peek() != '}') {
                do {
                    String key = parseString();
                    expect(':');
                    String value = peek() == '"' ? parseString() : parseNumber();
                    if (fields.putIfAbsent(key, value) != null) {
                        throw error("duplicate key \"" + key + "\"");
                    }
                } while (tryConsume(','));
            }
            expect('}');
            if (peek() != 0) {
                throw error("unexpected text after the object");
            }
            return fields;
        }

        private String parseString() {
            expect('"');
            var out = new StringBuilder();
            while (pos < text.length() && text.charAt(pos) != '"') {
                char c = text.charAt(pos++);
                if (c == '\\') {
                    if (pos >= text.length()) {
                        throw error("unfinished escape");
                    }
                    char escaped = text.charAt(pos++);
                    switch (escaped) {
                        case '"', '\\', '/' -> out.append(escaped);
                        case 'n' -> out.append('\n');
                        case 't' -> out.append('\t');
                        default -> throw error("unsupported escape \\" + escaped);
                    }
                } else {
                    out.append(c);
                }
            }
            if (pos >= text.length()) {
                throw error("unterminated string");
            }
            pos++;  // closing quote
            return out.toString();
        }

        private String parseNumber() {
            int start = pos;
            while (pos < text.length() && "+-0123456789.eE".indexOf(text.charAt(pos)) >= 0) {
                pos++;
            }
            if (start == pos) {
                throw error("expected a string or a number");
            }
            return text.substring(start, pos);
        }

        private void expect(char expected) {
            if (peek() != expected) {
                throw error("expected '" + expected + "'");
            }
            pos++;
        }

        private boolean tryConsume(char c) {
            if (peek() == c) {
                pos++;
                return true;
            }
            return false;
        }

        /** Skips whitespace and returns the next character, or {@code 0} at the end of the line. */
        private char peek() {
            while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
            return pos < text.length() ? text.charAt(pos) : 0;
        }

        private IllegalArgumentException error(String message) {
            return new IllegalArgumentException("line " + lineNumber + ": " + message + " at column " + (pos + 1));
        }
    }
}
