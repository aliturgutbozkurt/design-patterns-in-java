package io.github.aliturgutbozkurt.patterns.m01.examples.srp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m01.examples.srp.gradebook.after.GradeReport;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.gradebook.after.GradingScale;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.gradebook.after.ScoreParser;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.gradebook.after.StudentScores;
import io.github.aliturgutbozkurt.patterns.m01.examples.srp.gradebook.before.GradeBook;
import io.github.aliturgutbozkurt.patterns.m01.support.Console;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GradeBookTest {

    static final String CSV = """
            Ada,95,88,92
            Alan,72,65,80

            Grace,90,89.97,90
            Linus,40,55,50
            """;

    static final String REFERENCE_REPORT = """
            Student  Average  Grade
            Ada        91.67  AA
            Alan       72.33  CC
            Grace      89.99  BA
            Linus      48.33  FF
            Class average: 75.58
            """;

    static String refactoredReport(String csv) {
        return new GradeReport(new GradingScale()).render(new ScoreParser().parse(csv));
    }

    @Test
    void godClassProducesTheReferenceReport() {
        assertThat(new GradeBook().report(CSV)).isEqualTo(REFERENCE_REPORT);
    }

    @Test
    void refactoredVersionProducesTheSameReport() {
        assertThat(refactoredReport(CSV)).isEqualTo(new GradeBook().report(CSV));
    }

    @ParameterizedTest
    @CsvSource({"100, AA", "90.00, AA", "89.99, BA", "85, BA", "80, BB", "75, CB", "70, CC", "65, DC", "60, DD",
                "50, FD", "49.99, FF", "0, FF"})
    void mapsAveragesToLetterGrades(String average, String letter) {
        assertThat(new GradingScale().letterFor(new BigDecimal(average))).isEqualTo(letter);
    }

    @Test
    void gradingScaleRejectsAveragesOutsideZeroToHundred() {
        assertThatIllegalArgumentException().isThrownBy(() -> new GradingScale().letterFor(new BigDecimal("100.01")));
        assertThatIllegalArgumentException().isThrownBy(() -> new GradingScale().letterFor(new BigDecimal("-1")));
    }

    @Test
    void studentAverageIsRoundedHalfEvenToTwoDecimals() {
        var scores = new StudentScores("Grace", List.of(new BigDecimal("90"), new BigDecimal("89.97"), new BigDecimal("90")));
        assertThat(scores.average()).isEqualTo(new BigDecimal("89.99"));
    }

    @Test
    void parserSkipsBlankLinesAndKeepsOrder() {
        assertThat(new ScoreParser().parse(CSV)).extracting(StudentScores::name)
                .containsExactly("Ada", "Alan", "Grace", "Linus");
    }

    @Test
    void parserReportsTheLineNumberOfMalformedInput() {
        var parser = new ScoreParser();
        assertThatIllegalArgumentException().isThrownBy(() -> parser.parse("Ada,90\nAlan\n"))
                .withMessage("line 2: expected a name and at least one score");
        assertThatIllegalArgumentException().isThrownBy(() -> parser.parse("Ada,90\n\nAlan,abc\n"))
                .withMessage("line 3: not a number: abc");
        assertThatIllegalArgumentException().isThrownBy(() -> parser.parse("Ada,120\n"))
                .withMessage("line 1: score out of range 0-100: 120");
    }

    @Test
    void reportLayoutCanBeTestedWithoutParsing() {
        var report = new GradeReport(new GradingScale())
                .render(List.of(new StudentScores("Bob", List.of(new BigDecimal("70")))));
        assertThat(report).isEqualTo("""
                Student  Average  Grade
                Bob        70.00  CC
                Class average: 70.00
                """);
    }

    @Test
    void demoPrintsBothVersions() {
        assertThat(Console.capture(() -> GradeBookDemo.main(new String[0]))).isEqualTo(
                "== before: parse + grade + print in one class ==\n"
                        + REFERENCE_REPORT
                        + "== after: parser, grading scale, report ==\n"
                        + REFERENCE_REPORT
                        + "same text? true\n");
    }
}
