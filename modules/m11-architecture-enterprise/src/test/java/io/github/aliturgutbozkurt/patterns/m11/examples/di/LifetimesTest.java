package io.github.aliturgutbozkurt.patterns.m11.examples.di;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.m11.examples.di.lifetimes.Basket;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.lifetimes.PriceList;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.lifetimes.RequestScope;
import io.github.aliturgutbozkurt.patterns.m11.examples.di.lifetimes.ShopCompositionRoot;
import io.github.aliturgutbozkurt.patterns.m11.support.Console;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class LifetimesTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-30T10:00:00Z"), ZoneOffset.UTC);

    private final List<String> disk = new ArrayList<>();
    private final ShopCompositionRoot root = ShopCompositionRoot.production(CLOCK, disk::add);

    @Test
    void applicationLifetimeObjectIsTheSameInEveryRequest() {
        PriceList first;
        try (RequestScope request = root.beginRequest()) {
            first = request.priceList();
        }
        try (RequestScope request = root.beginRequest()) {
            assertThat(request.priceList()).isSameAs(first).isSameAs(root.priceList());
        }
    }

    @Test
    void perRequestObjectIsSharedWithinARequestAndNewInTheNext() {
        Basket first;
        try (RequestScope request = root.beginRequest()) {
            first = request.basket();
            first.add("book");
            assertThat(request.basket()).isSameAs(first);
        }
        try (RequestScope request = root.beginRequest()) {
            assertThat(request.basket()).isNotSameAs(first);
            assertThat(request.basket().items()).isEmpty();
        }
    }

    @Test
    void transientObjectIsNewOnEveryLookup() {
        assertThat(root.requestLog()).isNotSameAs(root.requestLog());
        assertThat(root.requestLog().number()).isEqualTo(3);
    }

    @Test
    void checkoutWritesTheTotalWithTheInjectedClock() {
        try (RequestScope request = root.beginRequest()) {
            request.basket().add("book");
            request.basket().add("pen");
            assertThat(request.checkout()).isEqualTo(new BigDecimal("27.00"));
        }
        assertThat(disk).containsExactly("orders.audit 2026-09-30T10:00:00Z request 1 paid 27.00 for [book, pen]");
    }

    @Test
    void closeClosesResourcesInReverseCreationOrderAndIsIdempotent() {
        root.close();
        root.close();
        assertThat(disk).containsExactly("access.audit closed", "orders.audit closed");
    }

    @Test
    void rootCannotBeUsedAfterClose() {
        root.close();
        assertThatIllegalStateException().isThrownBy(root::beginRequest).withMessage("composition root is closed");
        assertThatIllegalStateException().isThrownBy(root::priceList);
        assertThatIllegalStateException().isThrownBy(root::requestLog);
    }

    @Test
    void requestScopeCannotBeUsedAfterClose() {
        RequestScope request = root.beginRequest();
        request.close();
        request.close();
        assertThatIllegalStateException().isThrownBy(request::basket).withMessage("request 1 is closed");
    }

    @Test
    void demoPrintsLifetimesAndReverseClosing() {
        assertThat(Console.capture(() -> LifetimesDemo.main(new String[0]))).isEqualTo("""
                request 1: same basket within the request: true
                  disk> orders.audit 2026-09-30T10:00:00Z request 1 paid 27.00 for [book, pen]
                request 2: new basket: true
                request 2: same price list: true
                  disk> orders.audit 2026-09-30T10:00:00Z request 2 paid 12.50 for [mug]
                  disk> access.audit 2026-09-30T10:00:00Z log 1: GET /basket
                  disk> access.audit 2026-09-30T10:00:00Z log 2: POST /checkout
                transient logs are different objects: true
                closing the root:
                  disk> access.audit closed
                  disk> orders.audit closed
                """);
    }
}
