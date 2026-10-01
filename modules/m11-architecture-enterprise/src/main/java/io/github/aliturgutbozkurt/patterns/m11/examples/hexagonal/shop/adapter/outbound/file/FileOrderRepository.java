package io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.adapter.outbound.file;

import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.application.port.outbound.OrderRepository;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Money;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Order;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderId;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.OrderLine;
import io.github.aliturgutbozkurt.patterns.m11.examples.hexagonal.shop.domain.Sku;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.stream.Collectors;

/**
 * Outbound adapter: one order per line, {@code order-1|alice|BOOK-1:2:2000,PEN-7:1:700} (unit prices in cents). A
 * new instance on the same file sees every order saved before — the "restart" test.
 *
 * @see "m11 lesson, section Ports and Adapters — PatternShop"
 */
public final class FileOrderRepository implements OrderRepository {

    private final Path file;

    public FileOrderRepository(Path file) {
        this.file = Objects.requireNonNull(file, "file");
    }

    @Override
    public void save(Order order) {
        Objects.requireNonNull(order, "order");
        if (order.customer().matches("(?s).*[|,:\\r\\n].*")) {
            throw new IllegalArgumentException("customer must not contain '|', ',', ':' or a line break: "
                    + order.customer());
        }
        SequencedMap<OrderId, Order> orders = load();
        orders.put(order.id(), order);
        List<String> lines = orders.values().stream().map(FileOrderRepository::format).toList();
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("cannot write " + file, e);
        }
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.ofNullable(load().get(Objects.requireNonNull(id, "id")));
    }

    @Override
    public int count() {
        return load().size();
    }

    private SequencedMap<OrderId, Order> load() {
        SequencedMap<OrderId, Order> orders = new LinkedHashMap<>();
        if (!Files.exists(file)) {
            return orders;
        }
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                Order order = parse(line);
                orders.put(order.id(), order);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("cannot read " + file, e);
        }
        return orders;
    }

    private static String format(Order order) {
        return order.id() + "|" + order.customer() + "|" + order.lines().stream()
                .map(line -> line.sku() + ":" + line.quantity() + ":" + line.unitPrice().cents())
                .collect(Collectors.joining(","));
    }

    private static Order parse(String text) {
        String[] fields = text.split("\\|", -1);
        if (fields.length != 3) {
            throw new IllegalStateException("corrupt order line: " + text);
        }
        List<OrderLine> lines = Arrays.stream(fields[2].split(",")).map(item -> {
            String[] parts = item.split(":");
            return new OrderLine(new Sku(parts[0]), Integer.parseInt(parts[1]), new Money(Long.parseLong(parts[2])));
        }).toList();
        return Order.restore(new OrderId(fields[0]), fields[1], lines);
    }
}
