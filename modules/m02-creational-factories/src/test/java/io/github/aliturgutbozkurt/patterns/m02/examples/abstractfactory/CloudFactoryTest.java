package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.cloud.AcmeCloud;
import io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.cloud.CloudFactory;
import io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.cloud.NimbusCloud;
import io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.cloud.ReportArchiver;
import io.github.aliturgutbozkurt.patterns.m02.support.Console;
import java.util.List;
import org.junit.jupiter.api.Test;

class CloudFactoryTest {

    @Test
    void storageAndQueueOfOneFactoryBelongToTheSameProvider() {
        for (CloudFactory cloud : List.of(new AcmeCloud(), new NimbusCloud())) {
            assertThat(cloud.storage().provider()).isEqualTo(cloud.queue().provider()).isEqualTo(cloud.name());
        }
    }

    @Test
    void acmeUsesItsOwnUriSchemeAndRegion() {
        var archiver = new ReportArchiver(new AcmeCloud());
        String uri = archiver.archive("q3.csv", "region,total");
        assertThat(uri).isEqualTo("acme://eu-west/reports/q3.csv");
        assertThat(archiver.notifications()).containsExactly("archived acme://eu-west/reports/q3.csv");
        assertThat(archiver.read(uri)).contains("region,total");
    }

    @Test
    void nimbusUsesItsOwnUriSchemeAndRegion() {
        var archiver = new ReportArchiver(new NimbusCloud());
        assertThat(archiver.archive("q3.csv", "region,total")).isEqualTo("nimbus://tr-central/reports/q3.csv");
    }

    @Test
    void storageDoesNotReadAnotherProvidersUris() {
        var acme = new ReportArchiver(new AcmeCloud());
        var nimbus = new ReportArchiver(new NimbusCloud());
        String uri = acme.archive("a.txt", "data");
        assertThat(nimbus.read(uri)).isEmpty();
    }

    @Test
    void demoArchivesWithBothProviders() {
        assertThat(Console.capture(() -> CloudDemo.main(new String[0]))).isEqualTo("""
                Acme: stored acme://eu-west/reports/q3.csv, queue: [archived acme://eu-west/reports/q3.csv]
                Nimbus: stored nimbus://tr-central/reports/q3.csv, queue: [archived nimbus://tr-central/reports/q3.csv]
                """);
    }
}
