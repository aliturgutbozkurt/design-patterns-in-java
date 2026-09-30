package io.github.aliturgutbozkurt.patterns.m01.examples.srp.gradebook.before;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * SRP violation: parsing the CSV input, the grading policy and the report layout all live in one method, so a new
 * input format, a new grading scale or a new layout all edit the same code.
 *
 * @see "m01 lesson, section SRP"
 */
public final class GradeBook {

    /** Reads {@code name,score,score,...} lines and returns the printed report. */
    public String report(String csv) {
        var text = new StringBuilder("%-8s%8s  %s".formatted("Student", "Average", "Grade")).append('\n');
        List<BigDecimal> averages = new ArrayList<>();
        String[] lines = csv.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].strip();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split(",");
            if (parts.length < 2) {
                throw new IllegalArgumentException("line " + (i + 1) + ": expected a name and at least one score");
            }
            BigDecimal sum = BigDecimal.ZERO;
            for (int j = 1; j < parts.length; j++) {
                BigDecimal score;
                try {
                    score = new BigDecimal(parts[j].strip());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("line " + (i + 1) + ": not a number: " + parts[j].strip(), e);
                }
                if (score.signum() < 0 || score.compareTo(BigDecimal.valueOf(100)) > 0) {
                    throw new IllegalArgumentException("line " + (i + 1) + ": score out of range 0-100: " + score);
                }
                sum = sum.add(score);
            }
            BigDecimal average = sum.divide(BigDecimal.valueOf(parts.length - 1L), 2, RoundingMode.HALF_EVEN);
            averages.add(average);
            String letter;
            if (average.compareTo(BigDecimal.valueOf(90)) >= 0) {
                letter = "AA";
            } else if (average.compareTo(BigDecimal.valueOf(85)) >= 0) {
                letter = "BA";
            } else if (average.compareTo(BigDecimal.valueOf(80)) >= 0) {
                letter = "BB";
            } else if (average.compareTo(BigDecimal.valueOf(75)) >= 0) {
                letter = "CB";
            } else if (average.compareTo(BigDecimal.valueOf(70)) >= 0) {
                letter = "CC";
            } else if (average.compareTo(BigDecimal.valueOf(65)) >= 0) {
                letter = "DC";
            } else if (average.compareTo(BigDecimal.valueOf(60)) >= 0) {
                letter = "DD";
            } else if (average.compareTo(BigDecimal.valueOf(50)) >= 0) {
                letter = "FD";
            } else {
                letter = "FF";
            }
            text.append("%-8s%8s  %s".formatted(parts[0].strip(), average.toPlainString(), letter)).append('\n');
        }
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal average : averages) {
            total = total.add(average);
        }
        BigDecimal classAverage = averages.isEmpty()
                ? BigDecimal.ZERO.setScale(2)
                : total.divide(BigDecimal.valueOf(averages.size()), 2, RoundingMode.HALF_EVEN);
        text.append("Class average: ").append(classAverage.toPlainString()).append('\n');
        return text.toString();
    }
}
