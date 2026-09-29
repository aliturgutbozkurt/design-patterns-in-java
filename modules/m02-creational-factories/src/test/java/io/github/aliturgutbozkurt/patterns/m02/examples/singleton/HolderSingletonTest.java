package io.github.aliturgutbozkurt.patterns.m02.examples.singleton;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.github.aliturgutbozkurt.patterns.m02.examples.singleton.holder.CurrencyTable;
import io.github.aliturgutbozkurt.patterns.m02.support.Console;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

class HolderSingletonTest {

    /**
     * Loads {@link CurrencyTable} in a fresh class loader, so this test sees an untouched class no matter which tests
     * ran before it in this JVM.
     */
    @Test
    void isCreatedOnFirstUseNotWhenTheClassIsLoaded() throws Exception {
        URL classes = CurrencyTable.class.getProtectionDomain().getCodeSource().getLocation();
        try (var loader = new URLClassLoader(new URL[] {classes}, null)) {
            Class<?> fresh = Class.forName(CurrencyTable.class.getName(), true, loader);
            assertThat(fresh).isNotSameAs(CurrencyTable.class);
            assertThat(fresh.getMethod("creations").invoke(null)).isEqualTo(0);
            Object first = fresh.getMethod("getInstance").invoke(null);
            assertThat(fresh.getMethod("creations").invoke(null)).isEqualTo(1);
            assertThat(fresh.getMethod("getInstance").invoke(null)).isSameAs(first);
        }
    }

    @Test
    void thousandVirtualThreadsSeeOneInstanceCreatedOnce() throws Exception {
        Set<CurrencyTable> seen = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<>()));
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000; i++) {
                executor.submit(() -> seen.add(CurrencyTable.getInstance()));
            }
        }
        assertThat(seen).hasSize(1);
        assertThat(CurrencyTable.creations()).isEqualTo(1);
    }

    @Test
    void convertsToEuro() {
        var table = CurrencyTable.getInstance();
        assertThat(table.toEur(new BigDecimal("100"), "USD")).isEqualTo(new BigDecimal("92.00"));
        assertThat(table.toEur(new BigDecimal("1000"), "TRY")).isEqualTo(new BigDecimal("27.00"));
        assertThatIllegalArgumentException().isThrownBy(() -> table.toEur(BigDecimal.ONE, "XXX"))
                .withMessage("no rate for XXX");
    }

    @Test
    void demoPrintsConversionsAndIdentity() {
        assertThat(Console.capture(() -> HolderSingletonDemo.main(new String[0]))).isEqualTo("""
                100 USD = 92.00 EUR
                1000 TRY = 27.00 EUR
                same instance? true
                instances created: 1
                """);
    }
}
