package io.github.aliturgutbozkurt.patterns.m09.examples.features.catalogue;

/**
 * Catalogue row: Singleton. A lazily created, synchronised instance became an {@code enum} with one constant:
 * thread-safe, serialisation-safe and impossible to instantiate by reflection.
 *
 * @see "m09 lesson, section Patterns that became language features"
 */
public final class SingletonRow {

    private SingletonRow() {}

    /** Before: a private constructor, a static field and a synchronised accessor. */
    public static final class Classic {

        static final class Settings {
            // The one mutable static field in this module, kept on purpose: it is what the enum replaces (see m02).
            private static Settings instance;

            private Settings() {}

            static synchronized Settings getInstance() {
                if (instance == null) {
                    instance = new Settings();
                }
                return instance;
            }

            String currency() {
                return "EUR";
            }
        }

        private Classic() {}

        public static String run() {
            return "same instance: " + (Settings.getInstance() == Settings.getInstance())
                    + ", currency " + Settings.getInstance().currency();
        }
    }

    /** After: the JVM guarantees exactly one {@code INSTANCE}. */
    public static final class Modern {

        enum Settings {
            INSTANCE;

            String currency() {
                return "EUR";
            }
        }

        private Modern() {}

        public static String run() {
            return "same instance: " + (Settings.INSTANCE == Settings.valueOf("INSTANCE"))
                    + ", currency " + Settings.INSTANCE.currency();
        }
    }
}
