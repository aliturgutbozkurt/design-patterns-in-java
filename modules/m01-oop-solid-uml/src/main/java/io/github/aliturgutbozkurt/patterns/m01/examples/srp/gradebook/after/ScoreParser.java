package io.github.aliturgutbozkurt.patterns.m01.examples.srp.gradebook.after;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsibility: the input format. Changes only when the CSV layout changes.
 *
 * @see "m01 lesson, section SRP"
 */
public final class ScoreParser {

    private static final BigDecimal MAX_SCORE = BigDecimal.valueOf(100);

    /** Parses {@code name,score,score,...} lines; blank lines are skipped; errors name the 1-based line. */
    public List<StudentScores> parse(String csv) {
        List<StudentScores> result = new ArrayList<>();
        String[] lines = csv.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].strip();
            if (!line.isEmpty()) {
                result.add(parseLine(line, i + 1));
            }
        }
        return List.copyOf(result);
    }

    private static StudentScores parseLine(String line, int lineNumber) {
        String[] parts = line.split(",");
        if (parts.length < 2) {
            throw new IllegalArgumentException("line " + lineNumber + ": expected a name and at least one score");
        }
        List<BigDecimal> scores = new ArrayList<>();
        for (int j = 1; j < parts.length; j++) {
            scores.add(parseScore(parts[j].strip(), lineNumber));
        }
        return new StudentScores(parts[0].strip(), scores);
    }

    private static BigDecimal parseScore(String text, int lineNumber) {
        BigDecimal score;
        try {
            score = new BigDecimal(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("line " + lineNumber + ": not a number: " + text, e);
        }
        if (score.signum() < 0 || score.compareTo(MAX_SCORE) > 0) {
            throw new IllegalArgumentException("line " + lineNumber + ": score out of range 0-100: " + score);
        }
        return score;
    }
}
