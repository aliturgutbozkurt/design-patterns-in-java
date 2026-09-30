package io.github.aliturgutbozkurt.patterns.m04.examples.proxy.dynamic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

import io.github.aliturgutbozkurt.patterns.m04.support.Console;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;
import org.junit.jupiter.api.Test;

class DynamicProxyTest {

    /** Test-only interface whose methods declare a checked exception. */
    interface Greeter {
        String greet(String name) throws IOException;

        default String twice(String name) throws IOException {
            return greet(name) + " " + greet(name);
        }
    }

    private final AtomicLong fakeNanos = new AtomicLong();
    private final LongSupplier ticker = () -> fakeNanos.addAndGet(3_000_000);
    private final List<String> log = new ArrayList<>();
    private final InMemoryOrderRepository repository = new InMemoryOrderRepository();

    DynamicProxyTest() {
        repository.save("A-1", "2 x keyboard");
    }

    @Test
    void logsOneLinePerCallWithTheMethodAndTheElapsedTime() {
        OrderRepository timed = Proxies.timed(OrderRepository.class, repository, ticker, log::add);
        assertThat(timed.findById("A-1")).isEqualTo("2 x keyboard");
        assertThat(timed.findAllIds()).containsExactly("A-1");
        assertThat(log).containsExactly("OrderRepository.findById took 3 ms", "OrderRepository.findAllIds took 3 ms");
    }

    @Test
    void sameHelperWorksForTwoUnrelatedInterfaces() {
        OrderRepository orders = Proxies.timed(OrderRepository.class, repository, ticker, log::add);
        PriceList prices = Proxies.timed(PriceList.class,
                new InMemoryPriceList(Map.of("SKU-1", BigDecimal.TEN)), ticker, log::add);
        orders.findById("A-1");
        assertThat(prices.priceOf("SKU-1")).isEqualTo(BigDecimal.TEN);
        assertThat(log).containsExactly("OrderRepository.findById took 3 ms", "PriceList.priceOf took 3 ms");
    }

    @Test
    void uncheckedTargetExceptionReachesTheCallerUnchanged() {
        OrderRepository timed = Proxies.timed(OrderRepository.class, repository, ticker, log::add);
        assertThatThrownBy(() -> timed.findById("Z-9"))
                .isExactlyInstanceOf(NoSuchElementException.class)
                .hasMessage("no order Z-9");
        assertThat(log).containsExactly("OrderRepository.findById took 3 ms");   // failed calls are timed too
    }

    @Test
    void checkedTargetExceptionReachesTheCallerUnchanged() {
        var failure = new IOException("network down");
        Greeter target = name -> {
            throw failure;
        };
        Greeter timed = Proxies.timed(Greeter.class, target, ticker, log::add);
        Greeter readOnly = Proxies.readOnly(Greeter.class, target);
        assertThat(catchThrowable(() -> timed.greet("ada"))).isSameAs(failure);        // not UndeclaredThrowable
        assertThat(catchThrowable(() -> readOnly.greet("ada"))).isSameAs(failure);
    }

    @Test
    void defaultMethodsWorkAndTheirInnerCallsGoThroughTheProxy() throws IOException {
        OrderRepository timed = Proxies.timed(OrderRepository.class, repository, ticker, log::add);
        assertThat(timed.describe("A-1")).isEqualTo("A-1: 2 x keyboard");
        assertThat(log).containsExactly("OrderRepository.findById took 3 ms", "OrderRepository.describe took 9 ms");

        log.clear();
        Greeter greeter = Proxies.timed(Greeter.class, name -> "hi " + name, ticker, log::add);
        assertThat(greeter.twice("ada")).isEqualTo("hi ada hi ada");
        assertThat(log).containsExactly("Greeter.greet took 3 ms", "Greeter.greet took 3 ms", "Greeter.twice took 15 ms");
    }

    @Test
    void objectMethodsAreNotTimed() {
        OrderRepository timed = Proxies.timed(OrderRepository.class, repository, ticker, log::add);
        assertThat(timed.toString()).isEqualTo("timed InMemoryOrderRepository[A-1]");
        assertThat(timed.equals(timed)).isTrue();
        assertThat(timed.equals(repository)).isFalse();
        assertThat(timed.hashCode()).isEqualTo(System.identityHashCode(timed));
        assertThat(log).isEmpty();
    }

    @Test
    void mutatorMethodsAreBlockedAndNeverReachTheTarget() {
        OrderRepository readOnly = Proxies.readOnly(OrderRepository.class, repository);
        assertThatThrownBy(() -> readOnly.save("A-2", "x"))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("OrderRepository.save is read-only");
        assertThat(repository.findAllIds()).containsExactly("A-1");
        assertThat(readOnly.findById("A-1")).isEqualTo("2 x keyboard");
        assertThat(readOnly.describe("A-1")).isEqualTo("A-1: 2 x keyboard");

        var priceList = new InMemoryPriceList(Map.of("SKU-1", BigDecimal.ONE));
        PriceList readOnlyPrices = Proxies.readOnly(PriceList.class, priceList);
        assertThatThrownBy(() -> readOnlyPrices.changePrice("SKU-1", BigDecimal.TEN))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThat(priceList.priceOf("SKU-1")).isEqualTo(BigDecimal.ONE);
    }

    @Test
    void rejectsAClassInsteadOfAnInterface() {
        var prices = new InMemoryPriceList(Map.of());
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Proxies.readOnly(InMemoryPriceList.class, prices))
                .withMessageEndingWith("InMemoryPriceList is not an interface");
    }

    @Test
    void resultIsAProxyClass() {
        OrderRepository timed = Proxies.timed(OrderRepository.class, repository, ticker, log::add);
        assertThat(Proxy.isProxyClass(timed.getClass())).isTrue();
        assertThat(Proxy.isProxyClass(repository.getClass())).isFalse();
    }

    @Test
    void demoPrintsTimingsAndABlockedMutator() {
        assertThat(Console.capture(() -> DynamicProxyDemo.main(new String[0]))).isEqualTo("""
                timing proxy:
                  log: OrderRepository.findById took 3 ms
                  findById -> 2 x keyboard
                  log: OrderRepository.findById took 3 ms
                  log: OrderRepository.describe took 9 ms
                  describe -> A-2: 1 x monitor
                  log: OrderRepository.findById took 3 ms
                  findById -> NoSuchElementException: no order Z-9
                  log: PriceList.priceOf took 3 ms
                  priceOf -> 49.90
                read-only proxy:
                  findAllIds -> [A-1, A-2]
                  save -> OrderRepository.save is read-only
                  ids after the blocked save: [A-1, A-2]
                Proxy.isProxyClass: true
                """);
    }
}
