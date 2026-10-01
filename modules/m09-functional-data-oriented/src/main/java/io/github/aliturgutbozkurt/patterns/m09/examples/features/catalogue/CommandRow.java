package io.github.aliturgutbozkurt.patterns.m09.examples.features.catalogue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Catalogue row: Command. Command objects with {@code execute}/{@code undo} became sealed records (commands as data)
 * interpreted by one {@code switch}; a command without undo is just a {@link Runnable}.
 *
 * @see "m09 lesson, section Patterns that became language features"
 */
public final class CommandRow {

    private CommandRow() {}

    /**
     * Before: one class per command, each knowing how to do and undo itself.
     *
     * @see "m09 lesson, section Patterns that became language features"
     */
    public static final class Classic {

        interface Command {
            void execute(List<String> cart);

            void undo(List<String> cart);
        }

        record AddItem(String sku) implements Command {
            public void execute(List<String> cart) { cart.add(sku); }

            public void undo(List<String> cart) { cart.remove(sku); }
        }

        record RemoveItem(String sku) implements Command {
            public void execute(List<String> cart) { cart.remove(sku); }

            public void undo(List<String> cart) { cart.add(sku); }
        }

        private Classic() {}

        public static String run() {
            List<String> cart = new ArrayList<>();
            Deque<Command> history = new ArrayDeque<>();
            for (Command c : List.<Command>of(new AddItem("mug"), new AddItem("tee"), new RemoveItem("mug"))) {
                c.execute(cart);
                history.push(c);
            }
            history.pop().undo(cart);
            return cart + " history=" + history.size();
        }
    }

    /**
     * After: commands are plain data; behaviour lives in two exhaustive functions.
     *
     * @see "m09 lesson, section Patterns that became language features"
     */
    public static final class Modern {

        sealed interface Edit permits Add, Remove {}

        record Add(String sku) implements Edit {}

        record Remove(String sku) implements Edit {}

        private Modern() {}

        static void apply(List<String> cart, Edit edit) {
            switch (edit) {
                case Add(var sku) -> cart.add(sku);
                case Remove(var sku) -> cart.remove(sku);
            }
        }

        static Edit inverse(Edit edit) {
            return switch (edit) {
                case Add(var sku) -> new Remove(sku);
                case Remove(var sku) -> new Add(sku);
            };
        }

        public static String run() {
            List<String> cart = new ArrayList<>();
            Deque<Edit> history = new ArrayDeque<>();
            for (Edit e : List.<Edit>of(new Add("mug"), new Add("tee"), new Remove("mug"))) {
                apply(cart, e);
                history.push(e);
            }
            apply(cart, inverse(history.pop()));
            return cart + " history=" + history.size();
        }
    }
}
