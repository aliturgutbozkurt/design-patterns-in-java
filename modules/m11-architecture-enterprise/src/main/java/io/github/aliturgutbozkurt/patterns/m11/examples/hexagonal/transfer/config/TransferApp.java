package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.config;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.adapter.inbound.TextTransferController;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.adapter.outbound.InMemoryAccountStore;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.TransferService;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.Money;

/**
 * Composition root of the transfer hexagon: the only class that knows both the core and the adapters.
 *
 * @param controller the inbound adapter to drive the application with
 * @param accounts the outbound adapter (exposed so the demo can open accounts and print balances)
 * @see "m11 lesson, section Ports and Adapters"
 */
public record TransferApp(TextTransferController controller, InMemoryAccountStore accounts) {

    /** Wires the in-memory store into the service twice — once per port — and the controller in front. */
    public static TransferApp inMemory(Money limit) {
        var accounts = new InMemoryAccountStore();
        var service = new TransferService(accounts, accounts, limit);
        return new TransferApp(new TextTransferController(service), accounts);
    }
}
