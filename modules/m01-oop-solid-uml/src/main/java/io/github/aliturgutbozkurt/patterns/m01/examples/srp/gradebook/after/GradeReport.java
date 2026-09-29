package io.github.aliturgutbozkurt.patterns.m01.examples.srp.gradebook.after;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * Responsibility: the report layout. Uses a {@link GradingScale} but does not know how grades are decided.
 *
 * @see "m01 lesson, section SRP"
 */
public final class GradeReport {

    private final GradingScale scale;

    public GradeReport(GradingScale scale) {
        this.scale = Objects.requireNonNull(scale, "scale");
    }

    public String render(List<StudentScores> students) {
        var text = new StringBuilder(row("Student", "Average", "Grade"));
        for (StudentScores student : students) {
            BigDecimal average = student.average();
            text.append(row(student.name(), average.toPlainString(), scale.letterFor(average)));
        }
        text.append("Class average: ").append(classAverage(students).toPlainString()).append('\n');
        return text.toString();
    }

    private static BigDecimal classAverage(List<StudentScores> students) {
        if (students.isEmpty()) {
            return BigDecimal.ZERO.setScale(2);
        }
        BigDecimal total = students.stream().map(StudentScores::average).reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(students.size()), 2, RoundingMode.HALF_EVEN);
    }

    private static String row(String name, String average, String grade) {
        return "%-8s%8s  %s".formatted(name, average, grade) + '\n';
    }
}
