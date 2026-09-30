package io.github.aliturgutbozkurt.patterns.m09.examples.features.catalogue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Catalogue row: Strategy. An interface plus one class per algorithm became a {@link Comparator} (or any functional
 * interface) and a lambda.
 *
 * @see "m09 lesson, section Patterns that became language features"
 */
public final class StrategyRow {

    record Item(String name, long priceCents) {}

    static final List<Item> ITEMS =
            List.of(new Item("tee", 20_00), new Item("mug", 12_50), new Item("cap", 12_50), new Item("bag", 35_00));

    private StrategyRow() {}

    /** Before: a strategy interface and a named class for each ordering. */
    public static final class Classic {

        interface ItemOrder {
            int compare(Item a, Item b);
        }

        static final class ByPriceThenName implements ItemOrder {
            @Override
            public int compare(Item a, Item b) {
                int byPrice = Long.compare(a.priceCents(), b.priceCents());
                return byPrice != 0 ? byPrice : a.name().compareTo(b.name());
            }
        }

        private Classic() {}

        static List<String> sort(List<Item> items, ItemOrder order) {
            List<Item> copy = new ArrayList<>(items);
            copy.sort(order::compare);
            List<String> names = new ArrayList<>();
            for (Item item : copy) {
                names.add(item.name());
            }
            return names;
        }

        public static String run() {
            return sort(ITEMS, new ByPriceThenName()).toString();
        }
    }

    /** After: the JDK's strategy interface, built from key extractors. */
    public static final class Modern {

        static final Comparator<Item> BY_PRICE_THEN_NAME =
                Comparator.comparingLong(Item::priceCents).thenComparing(Item::name);

        private Modern() {}

        public static String run() {
            return ITEMS.stream().sorted(BY_PRICE_THEN_NAME).map(Item::name).toList().toString();
        }
    }
}
