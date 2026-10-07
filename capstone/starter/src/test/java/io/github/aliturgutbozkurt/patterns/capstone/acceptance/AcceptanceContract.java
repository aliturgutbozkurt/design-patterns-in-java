package io.github.aliturgutbozkurt.patterns.capstone.acceptance;

import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShop;
import io.github.aliturgutbozkurt.patterns.capstone.api.PatternShopFactory;
import io.github.aliturgutbozkurt.patterns.capstone.api.ShopSettings;
import org.junit.jupiter.api.Timeout;

/**
 * Base of every acceptance contract. A binding supplies the shop factory (your composition root) and the root package
 * of the application (for the architecture rules and the pattern inventory). Each test gets a fresh shop seeded with
 * the demo data, created on first use. Every test times out after 10 s.
 */
@Timeout(10)
public abstract class AcceptanceContract {

    private ShopTestKit kit;

    /** The factory under test, e.g. {@code new ShopCompositionRoot()}. */
    protected abstract PatternShopFactory factory();

    /** The application's root package, e.g. {@code io.github.aliturgutbozkurt.patterns.capstone.shop}. */
    protected abstract String applicationRootPackage();

    /** This test's shop with {@link ShopSettings#defaults()}, created on first use. */
    protected final ShopTestKit kit() {
        if (kit == null) {
            kit = ShopTestKit.start(factory(), ShopSettings.defaults());
        }
        return kit;
    }

    /** Replaces this test's shop by a new one with {@code settings}. */
    protected final ShopTestKit kitWith(ShopSettings settings) {
        kit = ShopTestKit.start(factory(), settings);
        return kit;
    }

    /** {@code kit().shop()} */
    protected final PatternShop shop() {
        return kit().shop();
    }
}
