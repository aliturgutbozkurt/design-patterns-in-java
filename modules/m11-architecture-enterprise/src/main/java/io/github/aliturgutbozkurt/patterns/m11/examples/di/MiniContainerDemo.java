package io.github.aliturgutbozkurt.patterns.m11.examples.di;

import io.github.aliturgutbozkurt.patterns.m11.examples.di.container.InMemoryReportRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.container.MiniContainer;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.container.ReportFormatter;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.container.ReportRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.container.ReportService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

/**
 * Run: {@code java modules/m11-architecture-enterprise/src/main/java/io/github/aliturgutbozkurt/patterns/m11/examples/di/MiniContainerDemo.java}
 *
 * @see "m11 lesson, section Dependency Injection"
 */
public final class MiniContainerDemo {

    private MiniContainerDemo() {}

    public static void main(String[] args) {
        MiniContainer container = new MiniContainer()
                .bindInstance(Clock.class, Clock.fixed(Instant.parse("2026-09-30T10:00:00Z"), ZoneOffset.UTC))
                .bind(ReportRepository.class, InMemoryReportRepository.class)
                .singleton(ReportRepository.class);

        ReportService service = container.get(ReportService.class); // the graph is built by reflection
        System.out.println(service.dailyReport());
        System.out.println("repository is a singleton: "
                + (container.get(ReportRepository.class) == container.get(ReportRepository.class)));
        System.out.println("formatter is created fresh: "
                + (container.get(ReportFormatter.class) != container.get(ReportFormatter.class)));

        MiniContainer forgetful = new MiniContainer().bindInstance(Clock.class, Clock.systemUTC());
        try {
            forgetful.get(ReportService.class); // compiles fine — the mistake shows up only now
        } catch (IllegalStateException e) {
            System.out.println("forgotten binding, found only at run time: " + e.getMessage());
        }
    }
}
