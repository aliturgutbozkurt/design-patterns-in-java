package io.github.aliturgutbozkurt.patterns.capstone.reference.adapter.in.cli;

import io.github.aliturgutbozkurt.patterns.capstone.api.cart.CartUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.catalogue.CatalogueUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.checkout.CheckoutUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.cli.CommandLine;
import io.github.aliturgutbozkurt.patterns.capstone.api.fulfilment.FulfilmentUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.order.OrderUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.DesignPattern;
import io.github.aliturgutbozkurt.patterns.capstone.api.pattern.PatternRole;
import io.github.aliturgutbozkurt.patterns.capstone.api.pricing.PricingUseCase;
import io.github.aliturgutbozkurt.patterns.capstone.api.report.ReportUseCase;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Inbound adapter: the line-based CLI over the use cases. A command table maps each command word to a
 * {@link CliCommand}; the adapter only splits the line, looks the command up and maps failures to {@code USAGE …}
 * (bad arguments) or {@code ERROR …} (rejected by a use case) — it never throws for bad input.
 *
 * @see "capstone guide §1 Pattern map — Command"
 */
@PatternRole(value = DesignPattern.COMMAND, role = "invoker (command table)")
@PatternRole(value = DesignPattern.PORTS_AND_ADAPTERS, role = "inbound adapter")
public final class CliAdapter implements CommandLine {

    private static final Set<String> GROUPS = Set.of("product", "cart", "order", "report");

    private final Map<String, CliCommands.Entry> commands;

    public CliAdapter(CatalogueUseCase catalogue, CartUseCase carts, PricingUseCase pricing,
                      CheckoutUseCase checkout, OrderUseCase orders, FulfilmentUseCase fulfilment,
                      ReportUseCase reports) {
        this.commands = new CliCommands(catalogue, carts, pricing, checkout, orders, fulfilment, reports).table();
    }

    @Override
    public String execute(String line) {
        String trimmed = line.strip();
        if (trimmed.isEmpty()) {
            return "";
        }
        String[] words = trimmed.split("\\s+");
        int keyLength = GROUPS.contains(words[0]) && words.length > 1 ? 2 : 1;
        String key = String.join(" ", Arrays.copyOf(words, keyLength));
        if (key.equals("help")) {
            return help();
        }
        CliCommands.Entry entry = commands.get(key);
        if (entry == null) {
            return "ERROR unknown command: " + key;
        }
        String[] split = trimmed.split("\\s+", keyLength + 1);
        String rest = split.length > keyLength ? split[keyLength].strip() : "";
        List<String> tokens = rest.isEmpty() ? List.of() : List.of(rest.split("\\s+"));
        try {
            return entry.command().run(new CliArgs(tokens, rest));
        } catch (UsageException e) {
            return "USAGE " + entry.usage();
        } catch (RuntimeException e) {
            return "ERROR " + e.getMessage(); // the use case rejected the request: report it, keep the CLI running
        }
    }

    private String help() {
        return "Commands:\n" + commands.values().stream().map(entry -> "  " + entry.usage())
                .collect(Collectors.joining("\n")) + "\n  help";
    }
}
