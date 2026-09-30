package io.github.aliturgutbozkurt.patterns.m09.examples.features.shop.modern;

import java.util.Objects;

/**
 * Invoice lines as data. This hierarchy keeps a name on purpose: it is the domain vocabulary, and the compiler checks
 * every {@code switch} over it.
 *
 * @see "m09 lesson, section Patterns that became language features — the combined effect"
 */
public sealed interface LineItem permits LineItem.PhysicalItem, LineItem.DigitalItem, LineItem.GiftCard {

    record PhysicalItem(String sku, long unitPriceCents, int quantity, int gramsEach) implements LineItem {
        public PhysicalItem {
            Objects.requireNonNull(sku, "sku");
        }
    }

    record DigitalItem(String sku, long priceCents) implements LineItem {
        public DigitalItem {
            Objects.requireNonNull(sku, "sku");
        }
    }

    record GiftCard(String code, long valueCents) implements LineItem {
        public GiftCard {
            Objects.requireNonNull(code, "code");
        }
    }
}
