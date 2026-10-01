package io.github.aliturgutbozkurt.patterns.m01.examples.isp;

import io.github.aliturgutbozkurt.patterns.m01.examples.isp.after.ArchiveService;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.after.BasicPrinter;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.after.OfficeMachine;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.after.PrintQueue;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.after.Printer;
import io.github.aliturgutbozkurt.patterns.m01.examples.isp.before.MultiFunctionDevice;
import java.util.List;

/**
 * Run: {@code java modules/m01-oop-solid-uml/src/main/java/io/github/aliturgutbozkurt/patterns/m01/examples/isp/IspDemo.java}
 *
 * @see "m01 lesson, section ISP"
 */
public final class IspDemo {

    private IspDemo() {}

    public static void main(String[] args) {
        System.out.println("== before: one fat MultiFunctionDevice interface ==");
        MultiFunctionDevice device = new io.github.aliturgutbozkurt.patterns.m01.examples.isp.before.BasicPrinter();
        System.out.println(device.print("report.pdf"));
        try {
            device.scan("page-1");
        } catch (UnsupportedOperationException e) {
            System.out.println("scan -> UnsupportedOperationException: " + e.getMessage());
        }

        System.out.println("== after: small role interfaces ==");
        var machine = new OfficeMachine();
        for (Printer printer : List.of(new BasicPrinter(), machine)) {
            var queue = new PrintQueue(printer);
            queue.submit("report.pdf");
            queue.submit("memo.txt");
            System.out.println("queue on " + printer.getClass().getSimpleName() + " -> " + queue.printAll());
        }
        System.out.println("archive -> " + new ArchiveService(machine).archive(List.of("contract-p1", "contract-p2")));
        System.out.println(machine.fax("memo.txt", "0312 000 00 00"));
    }
}
