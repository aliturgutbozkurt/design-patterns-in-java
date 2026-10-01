package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.adapter.inbound.TextTransferController;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.TransferCommand;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.TransferResult.Rejected;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.application.TransferResult.Transferred;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.config.TransferDemo;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.AccountId;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.transfer.domain.Money;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class TextTransferControllerTest {

    private final List<TransferCommand> received = new ArrayList<>();

    @Test
    void translatesTextIntoACommandAndTransferredIntoOk() {
        var controller = new TextTransferController(command -> {
            received.add(command);
            return new Transferred(command.from(), command.to(), command.amount());
        });
        assertThat(controller.handle("transfer A-1 A-2 25.00")).isEqualTo("OK");
        assertThat(received).containsExactly(
                new TransferCommand(new AccountId("A-1"), new AccountId("A-2"), Money.of("25.00")));
    }

    @Test
    void mapsRejectedToRejectedWithTheReason() {
        var controller = new TextTransferController(command -> new Rejected("insufficient funds"));
        assertThat(controller.handle("  transfer   A-1 A-2 25  ")).isEqualTo("REJECTED insufficient funds");
    }

    @Test
    void printsUsageForMalformedInputWithoutCallingTheUseCase() {
        var controller = new TextTransferController(command -> {
            received.add(command);
            return new Rejected("never");
        });
        for (String line : List.of("", "transfer A-1 A-2", "send A-1 A-2 1.00", "transfer A-1 A-2 lots",
                "transfer A-1 A-2 -5.00", "transfer A-1 A-2 1.001")) {
            assertThat(controller.handle(line)).as(line).isEqualTo("usage: transfer <from> <to> <amount>");
        }
        assertThat(received).isEmpty();
    }

    @Test
    void demoPrintsEveryOutcomeAndTheFinalBalances() {
        assertThat(Console.capture(() -> TransferDemo.main(new String[0]))).isEqualTo("""
                > transfer A-1 A-2 25.00
                OK
                > transfer A-2 A-1 500.00
                REJECTED insufficient funds
                > transfer A-1 A-1 1.00
                REJECTED same source and target account
                > transfer A-1 A-9 1.00
                REJECTED unknown account: A-9
                > transfer A-1 A-2 5000.00
                REJECTED amount exceeds the limit of 1000.00
                > send money
                usage: transfer <from> <to> <amount>
                balances: {A-1=75.00, A-2=45.00}
                """);
    }
}
