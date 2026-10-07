package io.github.aliturgutbozkurt.patterns.capstone.reference.application.render;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.capstone.api.model.CustomerId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Money;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderId;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.OrderStatus;
import io.github.aliturgutbozkurt.patterns.capstone.api.model.Sku;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.Report;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.StatementLine;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.StockLine;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ReportRendererTest {

    private final Report statement = new Report.CustomerStatementReport(new CustomerId("alice"), List.of(
            new StatementLine(OrderId.of(1), LocalDate.of(2026, 11, 16), OrderStatus.PAID, Money.of("10.00"))),
            Money.of("10.00"));

    @Test
    void textHasTitleRowsAndTotal() {
        assertThat(new TextRenderer().render(statement))
                .isEqualTo("Statement for alice\norder-1 | 2026-11-16 | PAID | 10.00\nTotal spent | 10.00\n");
    }

    @Test
    void csvHasHeaderRowsNoTotalAndQuotesCommasAndQuotes() {
        Report inventory = new Report.InventoryReport(List.of(new StockLine(new Sku("BOK-003"), "A, \"B\"", 4, true)));

        assertThat(new CsvRenderer().render(statement)).isEqualTo("order,date,status,total\norder-1,2026-11-16,PAID,10.00\n");
        assertThat(new CsvRenderer().render(inventory)).isEqualTo("sku,name,stock,low\nBOK-003,\"A, \"\"B\"\"\",4,yes\n");
    }

    @Test
    void theTemplateFixesTheOrderOfTheParts() {
        ReportRenderer tagged = new ReportRenderer() {
            @Override
            protected String heading(Table table) {
                return "H";
            }

            @Override
            protected String row(List<String> fields) {
                return "R";
            }

            @Override
            protected Optional<String> total(List<String> fields) {
                return Optional.of("T");
            }
        };

        assertThat(tagged.render(statement)).isEqualTo("H\nR\nT\n");
    }
}
