package io.github.aliturgutbozkurt.patterns.m01.examples.isp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aliturgutbozkurt.patterns.m01.examples.isp.after.ArchiveService;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.after.BasicPrinter;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.after.DocumentScanner;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.after.Fax;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.after.OfficeMachine;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.after.PrintQueue;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.after.Printer;
import io.github.aliturgutbozkurt.patterns.m01.support.Console;
import java.util.List;
import org.junit.jupiter.api.Test;

class OfficeDeviceTest {

    /** Documents the smell on purpose: the fat interface forces a method the class cannot honour. */
    @Test
    void beforeBasicPrinterMustThrowForScanAndFax() {
        var printer = new io.github.aliturgutbozkurt.patterns.m01.examples.isp.before.BasicPrinter();
        assertThat(printer.print("report.pdf")).isEqualTo("BasicPrinter printed report.pdf");
        assertThatThrownBy(() -> printer.scan("page-1")).isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("BasicPrinter cannot scan");
        assertThatThrownBy(() -> printer.fax("memo.txt", "0312")).isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("BasicPrinter cannot fax");
    }

    @Test
    void afterPrintQueueAcceptsAnyPrinterIncludingALambda() {
        Printer pdfPrinter = document -> "saved " + document + " as PDF";
        for (Printer printer : List.of(new BasicPrinter(), new OfficeMachine(), pdfPrinter)) {
            var queue = new PrintQueue(printer);
            queue.submit("a.txt");
            queue.submit("b.txt");
            assertThat(queue.printAll()).hasSize(2).allMatch(line -> line.contains("a.txt") || line.contains("b.txt"));
        }
    }

    @Test
    void afterPrintQueueEmptiesAfterPrinting() {
        var queue = new PrintQueue(new BasicPrinter());
        queue.submit("a.txt");
        assertThat(queue.printAll()).containsExactly("BasicPrinter printed a.txt");
        assertThat(queue.printAll()).isEmpty();
    }

    @Test
    void afterBasicPrinterPlaysOnlyThePrinterRole() {
        assertThat(DocumentScanner.class.isAssignableFrom(BasicPrinter.class)).isFalse();
        assertThat(Fax.class.isAssignableFrom(BasicPrinter.class)).isFalse();
    }

    @Test
    void afterOfficeMachinePlaysAllThreeRoles() {
        var machine = new OfficeMachine();
        assertThat(machine).isInstanceOf(Printer.class).isInstanceOf(DocumentScanner.class).isInstanceOf(Fax.class);
        assertThat(machine.fax("memo.txt", "0312 000 00 00")).isEqualTo("OfficeMachine faxed memo.txt to 0312 000 00 00");
    }

    @Test
    void afterArchiveServiceNeedsOnlyAScanner() {
        DocumentScanner fake = page -> "fake scan of " + page;
        assertThat(new ArchiveService(fake).archive(List.of("p1", "p2")))
                .containsExactly("archived: fake scan of p1", "archived: fake scan of p2");
    }

    @Test
    void demoShowsTheFatInterfaceAndTheRoles() {
        assertThat(Console.capture(() -> IspDemo.main(new String[0]))).isEqualTo("""
                == before: one fat MultiFunctionDevice interface ==
                BasicPrinter printed report.pdf
                scan -> UnsupportedOperationException: BasicPrinter cannot scan
                == after: small role interfaces ==
                queue on BasicPrinter -> [BasicPrinter printed report.pdf, BasicPrinter printed memo.txt]
                queue on OfficeMachine -> [OfficeMachine printed report.pdf, OfficeMachine printed memo.txt]
                archive -> [archived: OfficeMachine scanned contract-p1, archived: OfficeMachine scanned contract-p2]
                OfficeMachine faxed memo.txt to 0312 000 00 00
                """);
    }
}
