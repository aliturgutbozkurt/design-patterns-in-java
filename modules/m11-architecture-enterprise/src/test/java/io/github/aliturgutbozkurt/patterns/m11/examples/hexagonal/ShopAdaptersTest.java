package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.inbound.cli.CommandLineAdapter;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.file.FileOrderRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.payment.LegacyPaymentAdapter;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderCommand;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderCommand.LineRequest;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderResult.Placed;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.inbound.PlaceOrderResult.Rejected;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.config.ShopDemo;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderId;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderLine;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Sku;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ShopAdaptersTest {

    private static final List<OrderLine> LINES = List.of(
            new OrderLine(new Sku("BOOK-1"), 2, Money.of("20.00")), new OrderLine(new Sku("PEN-7"), 1, Money.of("7.00")));

    @TempDir
    Path directory;

    @Test
    void legacyPaymentAdapterMapsStatusCodes() {
        List<Long> cents = new ArrayList<>();
        assertThat(new LegacyPaymentAdapter((account, amount) -> {
            cents.add(amount);
            return 0;
        }).charge("alice", Money.of("47.00"))).isTrue();
        assertThat(cents).containsExactly(4700L);
        assertThat(new LegacyPaymentAdapter((account, amount) -> 51).charge("bob", Money.of("1.00"))).isFalse();
        assertThatIllegalStateException()
                .isThrownBy(() -> new LegacyPaymentAdapter((account, amount) -> 96).charge("eve", Money.of("1.00")))
                .withMessage("AcmePay failed with status code 96");
    }

    @Test
    void fileOrderRepositorySurvivesANewInstanceOnTheSamePath() throws IOException {
        Path file = directory.resolve("orders.txt");
        new FileOrderRepository(file).save(Order.place(new OrderId("order-1"), "alice", LINES));

        var afterRestart = new FileOrderRepository(file);
        Order loaded = afterRestart.findById(new OrderId("order-1")).orElseThrow();
        assertThat(loaded.customer()).isEqualTo("alice");
        assertThat(loaded.lines()).isEqualTo(LINES);
        assertThat(loaded.total()).isEqualTo(Money.of("47.00"));
        assertThat(loaded.pullEvents()).as("a restored order raises no event").isEmpty();
        assertThat(afterRestart.count()).isEqualTo(1);
        assertThat(Files.readAllLines(file)).containsExactly("order-1|alice|BOOK-1:2:2000,PEN-7:1:700");
    }

    @Test
    void fileOrderRepositoryReplacesAnOrderWithTheSameIdAndRejectsSeparators() {
        var repository = new FileOrderRepository(directory.resolve("orders.txt"));
        repository.save(Order.place(new OrderId("order-1"), "alice", LINES));
        repository.save(Order.restore(new OrderId("order-1"), "alice", LINES.subList(0, 1)));
        assertThat(repository.count()).isEqualTo(1);
        assertThat(repository.findById(new OrderId("order-1")).orElseThrow().total()).isEqualTo(Money.of("40.00"));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> repository.save(Order.place(new OrderId("order-2"), "a|b", LINES)));
    }

    @Test
    void commandLineAdapterParsesTheLineAndPrintsTheResult() {
        List<PlaceOrderCommand> received = new ArrayList<>();
        var cli = new CommandLineAdapter(command -> {
            received.add(command);
            return new Placed(new OrderId("order-1"), Money.of("47.00"));
        });
        assertThat(cli.handle("place alice BOOK-1:2 PEN-7:1")).isEqualTo("PLACED order-1 total 47.00");
        assertThat(received).containsExactly(new PlaceOrderCommand("alice",
                List.of(new LineRequest(new Sku("BOOK-1"), 2), new LineRequest(new Sku("PEN-7"), 1))));
        assertThat(new CommandLineAdapter(command -> new Rejected("payment declined")).handle("place bob MUG-3:1"))
                .isEqualTo("REJECTED payment declined");
    }

    @Test
    void commandLineAdapterPrintsUsageForMalformedInput() {
        var cli = new CommandLineAdapter(command -> {
            throw new AssertionError("the use case must not be called");
        });
        for (String line : List.of("", "place", "place alice", "buy alice BOOK-1:1", "place alice BOOK-1",
                "place alice BOOK-1:two", "place alice book-1:1", "place alice BOOK-1:1:1")) {
            assertThat(cli.handle(line)).as(line).isEqualTo("usage: place <customer> <sku>:<quantity>...");
        }
    }

    @Test
    void demoRunsTheSameSessionOnMemoryAndOnFiles() {
        assertThat(Console.capture(() -> ShopDemo.main(new String[0]))).isEqualTo("""
                == in memory
                > place alice BOOK-1:2 PEN-7:1
                PLACED order-1 total 47.00
                > place bob TOY-9:1
                REJECTED unknown product: TOY-9
                > place carol BOOK-1:30
                REJECTED payment declined
                > place dave
                usage: place <customer> <sku>:<quantity>...
                payment calls: [alice 4700, carol 60000], orders saved: 1
                published: [OrderPlaced[orderId=order-1, customer=alice, total=47.00]]
                == file backed
                > place alice BOOK-1:2 PEN-7:1
                PLACED order-1 total 47.00
                > place bob TOY-9:1
                REJECTED unknown product: TOY-9
                > place carol BOOK-1:30
                REJECTED payment declined
                > place dave
                usage: place <customer> <sku>:<quantity>...
                payment calls: [alice 4700, carol 60000], orders saved: 1
                published: [OrderPlaced[orderId=order-1, customer=alice, total=47.00]]
                after a restart: order-1 for alice, total 47.00
                > place erin MUG-3:1
                PLACED order-2 total 8.75
                """);
    }
}
