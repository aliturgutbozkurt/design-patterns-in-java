package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.adapter.inbound;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.TransferCommand;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.TransferMoneyUseCase;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.TransferResult.Rejected;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.TransferResult.Transferred;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.AccountId;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.Money;
import java.util.Objects;

/**
 * Inbound adapter: translates a text line into a {@link TransferCommand} and the result back into text. No business
 * rule lives here — swap it for an HTTP controller and the core does not change.
 *
 * @see "m11 lesson, section Ports and Adapters"
 */
public final class TextTransferController {

    static final String USAGE = "usage: transfer <from> <to> <amount>";

    private final TransferMoneyUseCase useCase;

    public TextTransferController(TransferMoneyUseCase useCase) {
        this.useCase = Objects.requireNonNull(useCase, "useCase");
    }

    /** Handles {@code transfer A-1 A-2 25.00}; answers {@code OK}, {@code REJECTED <reason>} or the usage line. */
    public String handle(String line) {
        String[] words = line.strip().split("\\s+");
        if (words.length != 4 || !words[0].equals("transfer")) {
            return USAGE;
        }
        Money amount;
        try {
            amount = Money.of(words[3]);
        } catch (IllegalArgumentException _) { // a malformed amount is a usage error: answer it, don't crash
            return USAGE;
        }
        var command = new TransferCommand(new AccountId(words[1]), new AccountId(words[2]), amount);
        return switch (useCase.transfer(command)) {
            case Transferred _ -> "OK";
            case Rejected(String reason) -> "REJECTED " + reason;
        };
    }
}
