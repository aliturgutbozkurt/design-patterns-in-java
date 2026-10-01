package io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory;

import io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.cloud.AcmeCloud;
import io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.cloud.CloudFactory;
import io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.cloud.NimbusCloud;
import io.github.aliturgutbozkurt.patterns.m02.examples.abstractfactory.cloud.ReportArchiver;
import java.util.List;

/**
 * Run: {@code java modules/m02-creational-factories/src/main/java/io/github/aliturgutbozkurt/patterns/m02/examples/abstractfactory/CloudDemo.java}
 *
 * @see "m02 lesson, section Abstract Factory"
 */
public final class CloudDemo {

    private CloudDemo() {}

    public static void main(String[] args) {
        for (CloudFactory cloud : List.of(new AcmeCloud(), new NimbusCloud())) {
            var archiver = new ReportArchiver(cloud);
            String uri = archiver.archive("q3.csv", "region,total");
            System.out.println(cloud.name() + ": stored " + uri + ", queue: " + archiver.notifications());
        }
    }
}
