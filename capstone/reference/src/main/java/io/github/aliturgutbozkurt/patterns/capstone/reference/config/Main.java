package io.github.aliturgutbozkurt.patterns.capstone.reference.config;

import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShop;
import io.github.aliturgutbozkurt.patterns.capstone.api.ShopEnvironment;
import io.github.aliturgutbozkurt.patterns.capstone.api.ShopSettings;
import io.github.aliturgutbozkurt.patterns.capstone.api.sim.ConsoleNotifications;
import io.github.aliturgutbozkurt.patterns.capstone.api.sim.DemoData;
import io.github.aliturgutbozkurt.patterns.capstone.api.sim.SimulatedPaymentApi;
import io.github.aliturgutbozkurt.patterns.capstone.api.sim.SimulatedWarehouse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.ZoneId;
import java.util.List;

/**
 * Runs the reference PatternShop on the command line with the simulated external systems: one command per line from
 * standard input, the response on standard output; {@code --demo} seeds the sample data.
 *
 * <pre>
 * ./mvnw -q -pl capstone/reference -am package -DskipTests
 * java -cp capstone/starter/target/classes:capstone/reference/target/classes \
 *      io.github.aliturgutbozkurt.patterns.capstone.reference.config.Main --demo
 * </pre>
 *
 * @see "capstone guide §2 Slice walkthrough — C6"
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        ShopEnvironment env = new ShopEnvironment(Clock.system(ZoneId.of("Europe/Istanbul")),
                new SimulatedPaymentApi(), new SimulatedWarehouse(), new ConsoleNotifications(System.out::println),
                error -> System.err.println("ERROR in event handler: " + error), ShopSettings.defaults());
        PatternShop shop = new ReferenceCompositionRoot().create(env);
        if (List.of(args).contains("--demo")) {
            DemoData.seed(shop);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        for (String line = in.readLine(); line != null; line = in.readLine()) {
            if (!line.isBlank()) {
                System.out.println(shop.cli().execute(line));
            }
        }
    }
}
